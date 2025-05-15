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
import org.greenflow.order.model.dto.OrderUpdateDto;
import org.greenflow.order.model.entity.Order;
import org.greenflow.order.output.event.RabbitMQProducer;
import org.greenflow.order.output.persistent.OrderRepository;
import org.greenflow.order.service.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private static final String FORBIDDEN_MESSAGE = "You do not have access to this resource";
    public static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";

    private final OrderRepository orderRepository;
    private final RabbitMQProducer rabbitMQProducer;

    public Order createOrder(@NotBlank String clientId, @NotBlank String clientEmail,
                             @Valid @NotNull OrderCreationDto orderDto) {
        Order order = OrderMapper.INSTANCE.toEntity(orderDto);
        order.setClientId(clientId);
        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        rabbitMQProducer.sendOrderOpeningMessage(order, clientEmail);

        order.setStatus(OrderStatus.OPEN);
        order = orderRepository.save(order);

        log.info("Client {} created order: {}", order.getClientId(), order.getId());
        return order;
    }

    public List<Order> getOrdersByOwnerId(@NotBlank String clientId) {
        return orderRepository.findAllByClientId(clientId);
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

    public Order updateOrder(@NotBlank String userId, @NotBlank String orderId,
                             @NotNull @Valid OrderUpdateDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), ORDER_NOT_FOUND_MESSAGE));
        if (!order.getClientId().equals(userId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), FORBIDDEN_MESSAGE);
        }
        order.setStartDate(orderDto.getStartDate());
        order.setDescription(orderDto.getDescription());
        return orderRepository.save(order);
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
}
