package org.greenflow.order.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.OrderAssignedMessageDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.order.model.constant.OrderStatus;
import org.greenflow.order.model.dto.OrderCreationDto;
import org.greenflow.order.model.dto.OrderDto;
import org.greenflow.order.model.dto.OrderItemCreationDto;
import org.greenflow.order.model.dto.OrderUpdateDto;
import org.greenflow.order.model.entity.Order;
import org.greenflow.order.model.entity.OrderItem;
import org.greenflow.order.output.event.RabbitMQProducer;
import org.greenflow.order.output.persistent.OrderRepository;
import org.greenflow.order.output.persistent.ServiceRepository;
import org.greenflow.order.service.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private static final String FORBIDDEN_MESSAGE = "You do not have access to this resource";
    public static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public OrderDto createOrder(@NotBlank String clientId, @NotBlank String clientEmail,
                                @Valid @NotNull OrderCreationDto orderDto) {
        Order order = OrderMapper.INSTANCE.toEntity(orderDto);
        order.setClientId(clientId);
        createOrderItems(order, orderDto.getOrderItems());
        order.setTotalPrice(calculateTotalPrice(order));
        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        if (order.getStartDate().isEqual(LocalDate.now()) || order.getStartDate().isAfter(LocalDate.now())) {
            order.setStatus(OrderStatus.OPEN);
            rabbitMQProducer.sendOrderOpeningMessage(order, clientEmail);
        }
        log.info("Client {} created order: {}", order.getClientId(), order.getId());
        return OrderMapper.INSTANCE.toDto(order);
    }

    private Order createOrderItems(Order order, List<OrderItemCreationDto> orderItemDtos) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (var itemDto : orderItemDtos) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setService(serviceRepository.findById(itemDto.getServiceId()).orElseThrow(() ->
                    new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Service not found with ID: "
                            + itemDto.getServiceId()))
            );
            orderItem.setQuantity(itemDto.getQuantity());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        log.debug("Created {} order items for order {}", orderItems.size(), order.getId());
        return order;
    }

    private static BigDecimal calculateTotalPrice(Order order) {
        return order.getOrderItems() == null ? BigDecimal.ZERO :
                order.getOrderItems().stream()
                        .map(item -> item.getService().getPricePerUnit()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderDto> getOrdersByOwnerId(@NotBlank String clientId) {
        return orderRepository.findAllByClientId(clientId).stream()
                .map(OrderMapper.INSTANCE::toDto)
                .toList();
    }

    public void deleteOrder(@NotBlank String clientId, @NotBlank String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), ORDER_NOT_FOUND_MESSAGE));
        if (!order.getClientId().equals(clientId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), FORBIDDEN_MESSAGE);
        }
        rabbitMQProducer.sendOrderDeletionMessage(order);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Client {} deleted order: {}", clientId, order.getId());
    }

    public OrderDto updateOrder(@NotBlank String userId, @NotBlank String orderId,
                                @NotNull @Valid OrderUpdateDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), ORDER_NOT_FOUND_MESSAGE));
        if (!order.getClientId().equals(userId)) {
            throw new GreenFlowException(403, FORBIDDEN_MESSAGE);
        }
        if (!order.getStatus().equals(OrderStatus.OPEN) && !order.getStatus().equals(OrderStatus.CREATED)) {
            throw new GreenFlowException(400, "Order cannot be updated in its current state");
        }
        order.setStartDate(orderDto.getStartDate());
        if (orderDto.getStartDate().isAfter(LocalDate.now()) || orderDto.getStartDate().isEqual(LocalDate.now())) {
            order.setStatus(OrderStatus.OPEN);
        } else if (orderDto.getStartDate().isBefore(LocalDate.now())) {
            order.setStatus(OrderStatus.CREATED);
        }
        order.setDescription(orderDto.getDescription());
        createOrderItems(order, orderDto.getOrderItems());
        order.setTotalPrice(calculateTotalPrice(order));
        orderRepository.save(order);

        //send order update message
        rabbitMQProducer.sendOrderUpdatingMessage(order);

        return OrderMapper.INSTANCE.toDto(order);
    }

    public void processOrderAssignedMessage(@NotNull OrderAssignedMessageDto orderMessage) {
        Order orderEntity = orderRepository.findById(orderMessage.getOrderId())
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), ORDER_NOT_FOUND_MESSAGE));
        if (orderEntity.getWorkerId() != null && orderEntity.getWorkerId().equals(orderMessage.getWorkerId())) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), "Order already assigned to this worker");
        }
        if (orderEntity.getWorkerId() != null && !orderEntity.getWorkerId().isEmpty()) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), "Order already assigned to a worker");
        }
        orderEntity.setWorkerId(orderMessage.getWorkerId());
        orderEntity.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(orderEntity);
        log.info("Order {} assigned to worker {}", orderMessage.getOrderId(), orderMessage.getWorkerId());
    }

    public OrderDto completeOrder(String workerId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(400, "Order not found"));
        if (!order.getStatus().equals(OrderStatus.ASSIGNED))
            throw new GreenFlowException(400, "Order status is not ASSIGNED");
        if (order.getWorkerId() == null)
            throw new GreenFlowException(400, "Order does not have assigned worker");
        if (!order.getWorkerId().equals(workerId))
            throw new GreenFlowException(403, "No access to this resource!");
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        rabbitMQProducer.sendBalanceUpdateMessagesForCompletedOrder(order);
        log.info("Order {} completed by worker {}", orderId, workerId);
        return OrderMapper.INSTANCE.toDto(order);
    }

    public List<OrderDto> getAssignedOrdersByWorkerId(String workerId) {
        return orderRepository.findAllByWorkerId(workerId).stream()
                .map(OrderMapper.INSTANCE::toDto)
                .toList();
    }
}
