package org.greenflow.openorder.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.OrderOpeningMessageDto;
import org.greenflow.openorder.service.OpenOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final OpenOrderService openOrderService;

    @RabbitListener(queues = RabbitMQConstants.ORDER_OPENING_QUEUE)
    public void consumeOrderOpeningMessage(OrderOpeningMessageDto order) {
        openOrderService.saveOpenOrder(order);
    }
}
