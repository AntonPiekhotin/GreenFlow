package org.greenflow.openorder.input.event;

import org.greenflow.common.model.constant.RabbitMQConstants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = RabbitMQConstants.ORDER_OPENING_QUEUE)
    public void consumeOrderOpeningMessage(String message) {
        System.out.println("Received order opening message: " + message);
    }
}
