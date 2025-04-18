package org.greenflow.order.output.producer;

import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreatedMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_EXCHANGE, RabbitMQConstants.ORDER_CREATION_QUEUE, message);
    }
}