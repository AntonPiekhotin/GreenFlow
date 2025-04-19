package org.greenflow.order.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.OrderAssignedMessageDto;
import org.greenflow.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.greenflow.common.model.constant.RabbitMQConstants.ORDER_ASSIGNED_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = ORDER_ASSIGNED_QUEUE)
    public void consumeOrderAssignedMessage(OrderAssignedMessageDto order) {
        orderService.processOrderAssignedMessage(order);
    }


}
