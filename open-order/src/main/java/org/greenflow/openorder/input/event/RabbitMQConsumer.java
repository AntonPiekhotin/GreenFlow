package org.greenflow.openorder.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.OrderDeletionMessageDto;
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
        log.info("Received order opening message: {}", order);
        openOrderService.saveOpenOrder(order);
    }

    @RabbitListener(queues = RabbitMQConstants.ORDER_DELETION_QUEUE)
    public void consumeOrderDeletionMessage(OrderDeletionMessageDto order) {
        log.info("Received order deletion message: {}", order);
        openOrderService.deleteOpenOrder(order);
    }
}
