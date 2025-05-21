package org.greenflow.equipment.output.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.PaymentCreationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    public static final String FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ = "Failed to send message to RabbitMQ";
    private final RabbitTemplate rabbitTemplate;


    public void sendPaymentCreationMessage(@NotNull PaymentCreationMessage message) {
        log.debug("Sending message to RabbitMQ: {}", message);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.PAYMENT_EXCHANGE, RabbitMQConstants.PAYMENT_CREATION_QUEUE,
                    message);
            log.info("Payment creation message sent to RabbitMQ: {}", message);
        } catch (Exception e) {
            log.error(FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
            throw new RuntimeException(FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
        }
    }
}