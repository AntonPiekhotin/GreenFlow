package org.greenflow.order.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.order.model.dto.OrderDto;
import org.greenflow.order.model.entity.Order;
import org.greenflow.order.output.persistent.OrderRepository;
import org.greenflow.order.output.event.RabbitMQProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private static final String FORBIDDEN_MESSAGE = "You do not have access to this resource";
    private final OrderRepository orderRepository;

    private final RabbitMQProducer rabbitMQProducer;

    public Order createOrder(@NotBlank String clientId, @Valid @NotNull OrderDto orderDto) {
        Order order = Order.fromDto(orderDto);
        order.setClientId(clientId);
        order = orderRepository.save(order);
        log.info("Client {} created order: {}", order.getClientId(), order.getId());

        sendMessageToOpenOrder(order);
        return order;
    }

    private void sendMessageToOpenOrder(Order order) {
        if (order.getStartDate().isAfter(LocalDate.now())) {
            try {
                rabbitMQProducer.sendOrderCreatedMessage(order.getId());
            } catch (Exception e) {
                throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Failed to send message to RabbitMQ");
            }
        }
    }

    public List<Order> getOrdersByOwnerId(@NotBlank String clientId) {
        return orderRepository.findAllByClientId(clientId);
    }

    public void deleteOrder(@NotBlank String clientId, @NotBlank String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Order not found"));
        if (!order.getClientId().equals(clientId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), FORBIDDEN_MESSAGE);
        }
        orderRepository.delete(order);
        log.info("Client {} deleted order: {}", clientId, order.getId());
    }

    public Order updateOrder(@NotBlank String userId, @NotBlank String orderId, @NotNull @Valid OrderDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GreenFlowException(HttpStatus.NOT_FOUND.value(), "Order not found"));
        if (!order.getClientId().equals(userId)) {
            throw new GreenFlowException(HttpStatus.FORBIDDEN.value(), FORBIDDEN_MESSAGE);
        }
        order.setStartDate(orderDto.getStartDate());
        order.setDescription(orderDto.getDescription());
        return orderRepository.save(order);
    }
}
