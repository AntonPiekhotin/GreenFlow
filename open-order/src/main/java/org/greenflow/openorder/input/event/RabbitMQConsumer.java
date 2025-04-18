package org.greenflow.openorder.input.event;

import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.OrderOpeningMessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = RabbitMQConstants.ORDER_OPENING_QUEUE)
    public void consumeOrderOpeningMessage(OrderOpeningMessageDto order) {
        //save to redis
        System.out.println("Received order opening message: " + order);
    }
}
