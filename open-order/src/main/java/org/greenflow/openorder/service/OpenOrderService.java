package org.greenflow.openorder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.EmailNotificationMessage;
import org.greenflow.common.model.dto.event.OrderAssignedMessage;
import org.greenflow.common.model.dto.event.OrderDeletionMessage;
import org.greenflow.common.model.dto.event.OrderOpeningMessage;
import org.greenflow.common.model.dto.event.OrderUpdatingMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.openorder.model.dto.OpenOrderDto;
import org.greenflow.openorder.model.dto.OpenOrdersRequestDto;
import org.greenflow.openorder.output.event.RabbitMQProducer;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Service class for managing open orders using Redis for geospatial data storage and retrieval.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenOrderService {

    private static final String GEO_KEY = "orders_geo"; // Redis key for storing geospatial data of orders.
    private static final String HASH_KEY_PREFIX = "order:"; // Prefix for Redis keys storing order details.

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Saves an open order to Redis, storing its geospatial data and details.
     *
     * @param order The order to be saved, represented as an OrderOpeningMessageDto.
     */
    public void saveOpenOrder(OrderOpeningMessage order) {
        log.debug("Saving open order id: {}", order.getOrderId());
        // Add the order's geospatial data (latitude and longitude) to Redis.
        redisTemplate.opsForGeo()
                .add(GEO_KEY, new Point(order.getLongitude(), order.getLatitude()), order.getOrderId());

        String orderJson;
        try {
            orderJson = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Store the serialized order details in Redis with a key prefixed by "order:".
        redisTemplate.opsForValue().set(HASH_KEY_PREFIX + order.getOrderId(), orderJson);
    }

    /**
     * Retrieves a list of open orders within a specified radius from a given location.
     *
     * @param request The request containing the location (latitude, longitude) and radius.
     * @return A list of OrderOpeningMessageDto objects representing the open orders within the radius.
     */
    public List<OpenOrderDto> getOpenOrdersWithinRadius(OpenOrdersRequestDto request) {
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();
        double radius = request.getRadius(); // Radius in kilometers.

        long start = System.currentTimeMillis();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = getOrderIdsWithinRadius(longitude, latitude, radius);
        if (results == null || results.getContent().isEmpty()) {
            log.info("No open orders found within the specified radius.");
            return List.of();
        }
        List<OpenOrderDto> openOrders = mapOrderIdsToOpenOrderDtos(results);
        log.info("Retrieved {} open orders within {} km radius from lat: {}, long: {} in {} ms",
                openOrders.size(), radius, latitude, longitude, System.currentTimeMillis() - start);
        return openOrders;
    }

    /**
     * Retrieves order IDs within a specified radius from a given location.
     *
     * @param longitude The longitude of the center point.
     * @param latitude  The latitude of the center point.
     * @param radius    The radius in kilometers.
     * @return A {@link GeoResults} object containing the order IDs within the specified radius.
     */
    private GeoResults<RedisGeoCommands.GeoLocation<String>> getOrderIdsWithinRadius(double longitude,
                                                                                     double latitude,
                                                                                     double radius) {
        return redisTemplate.opsForGeo()
                .radius(GEO_KEY, new Circle(new Point(longitude, latitude),
                        new Distance(radius, RedisGeoCommands.DistanceUnit.KILOMETERS)));
    }

    /**
     * Maps the order IDs to their corresponding OpenOrderDto objects.
     *
     * @param results The GeoResults containing the order IDs.
     * @return A list of OrderOpeningMessageDto objects.
     */
    private List<OpenOrderDto> mapOrderIdsToOpenOrderDtos(
            GeoResults<RedisGeoCommands.GeoLocation<String>> results) {
        return results.getContent().stream()
                .map(geoResult -> {
                    String orderId = geoResult.getContent().getName();
                    String orderJson = redisTemplate.opsForValue()
                            .get(HASH_KEY_PREFIX + orderId); // Retrieve the order details from Redis.
                    try {
                        return objectMapper.readValue(orderJson, OpenOrderDto.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize order with ID: {}", orderId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void updateOpenOrder(OrderUpdatingMessage updatedOrder) {
        log.debug("Updating open order id: {}", updatedOrder.orderId());
        String orderJson = redisTemplate.opsForValue().get(HASH_KEY_PREFIX + updatedOrder.orderId());
        if (orderJson == null) {
            log.error("Order with ID {} not found for update", updatedOrder.orderId());
            throw new GreenFlowException(404, "Order not found");
        }
        try {
            OpenOrderDto existingOrder = objectMapper.readValue(orderJson, OpenOrderDto.class);
            // Update the existing order with new details

            // Check if the start date is in the future
            if (LocalDate.now().isBefore(updatedOrder.startDate())) {
                deleteOpenOrderFromRedis(existingOrder.getOrderId());
                return;
            }
            existingOrder.setDescription(updatedOrder.description());
            existingOrder.setWage(updatedOrder.wage());

            // Save the updated order back to Redis
            redisTemplate.opsForValue().set(HASH_KEY_PREFIX + existingOrder.getOrderId(),
                    objectMapper.writeValueAsString(existingOrder));
            log.info("Updated open order with ID: {}", existingOrder.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("Failed to update order with ID: {}", updatedOrder.orderId(), e);
            throw new GreenFlowException(500, "Failed to update order", e);
        }

    }

    public void deleteOpenOrder(OrderDeletionMessage order) {
        deleteOpenOrderFromRedis(order.getOrderId());
    }

    private void deleteOpenOrderFromRedis(String orderId) {
        redisTemplate.opsForGeo().remove(GEO_KEY, orderId);
        redisTemplate.delete(HASH_KEY_PREFIX + orderId);
        log.info("Deleted open order with ID: {}", orderId);
    }

    @Transactional
    public void assignOrderToWorker(@NotBlank String orderId, @NotBlank String workerId) {
        var foundOrder = redisTemplate.opsForValue().get(HASH_KEY_PREFIX + orderId);
        if (foundOrder == null) {
            log.error("Order with ID {} not found", orderId);
            throw new GreenFlowException(400, "Order not found");
        }
        // send order assigned message to rabbitMQ, delete from redis
        try {
            var orderAssignedMessage = OrderAssignedMessage.builder()
                    .orderId(orderId)
                    .workerId(workerId)
                    .build();
            rabbitMQProducer.sendOrderAssignedMessage(orderAssignedMessage);
            deleteOpenOrderFromRedis(orderId);
            log.info("Order with ID {} assigned to worker {}", orderId, workerId);
            sendEmailToClient(foundOrder);
        } catch (Exception e) {
            log.error("Failed to assign order with ID {} to worker {}", orderId, workerId, e);
            throw new GreenFlowException(500, "Failed to assign order", e);
        }
    }

    public Collection<OpenOrderDto> getAllOpenOrders() {
        log.debug("Retrieving all open orders from Redis");
        return redisTemplate.keys(HASH_KEY_PREFIX + "*").stream()
                .map(key -> {
                    String orderJson = redisTemplate.opsForValue().get(key);
                    try {
                        return objectMapper.readValue(orderJson, OpenOrderDto.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize order with key: {}", key, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void sendEmailToClient(String orderString) {
        try {
            log.debug("Sending email to client: {}", orderString);
            OrderOpeningMessage order = objectMapper.readValue(orderString, OrderOpeningMessage.class);
            rabbitMQProducer.sendEmailMessage(EmailNotificationMessage.builder()
                            .userId(order.getClientId())
                            .subject("Order Assigned")
                            .text("Your order with ID " + order.getOrderId() + " has been assigned to a worker.")
                    .build());
            log.info("Email sent to client {} for order {}", order.getClientEmail(), order.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order string: {}", orderString, e);
            throw new RuntimeException(e);
        }

    }
}