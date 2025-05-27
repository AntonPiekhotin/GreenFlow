package org.greenflow.openorder.output.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.EmailNotificationMessage;
import org.greenflow.common.model.dto.event.OrderAssignedMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.greenflow.common.model.constant.RabbitMQConstants.NOTIFICATION_EXCHANGE;
import static org.greenflow.common.model.constant.RabbitMQConstants.NOTIFICATION_QUEUE;
import static org.greenflow.common.model.constant.RabbitMQConstants.ORDER_ASSIGNED_QUEUE;
import static org.greenflow.common.model.constant.RabbitMQConstants.ORDER_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    public static final String FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ = "Failed to send message to RabbitMQ ";
    private final RabbitTemplate rabbitTemplate;

    public void sendOrderAssignedMessage(OrderAssignedMessage order) {
        try {
            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ASSIGNED_QUEUE, order);
            log.info("Order assigned message sent to RabbitMQ: {}", order.getOrderId());
        } catch (Exception e) {
            onError(e);
        }
    }
    
    public void sendEmailMessage(EmailNotificationMessage email) {
        try {
            rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_QUEUE, email);
            log.info("Email notification message sent to RabbitMQ: {}", email.to());
        } catch (Exception e) {
            onError(e);
        }
    }

    private static void onError(Exception e) {
        log.error(FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ + "{}", e.getMessage());
        throw new GreenFlowException(500, FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
    }
}
