package org.greenflow.order.output.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.OrderOpeningMessageDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.order.model.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderOpeningMessage(Order order) {
        if (!LocalDateTime.now().isAfter(order.getStartDate().atStartOfDay())) {
            return;
        }
        try {
            var orderOpeningMessage = OrderOpeningMessageDto.builder()
                    .orderId(order.getId())
                    .longitude(order.getLongitude())
                    .latitude(order.getLatitude())
                    .description(order.getDescription())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_EXCHANGE, RabbitMQConstants.ORDER_OPENING_QUEUE,
                    orderOpeningMessage);
            log.info("Order opening message sent to RabbitMQ: {}", orderOpeningMessage.getOrderId());
        } catch (Exception e) {
            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to send message to RabbitMQ", e);
        }
    }
}