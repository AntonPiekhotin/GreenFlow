package org.greenflow.billing.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.billing.service.UserBalanceService;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.greenflow.common.model.constant.RabbitMQConstants.BALANCE_CHANGE_QUEUE;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final UserBalanceService userBalanceService;

    @RabbitListener(queues = BALANCE_CHANGE_QUEUE)
    public void consumeBalanceChangeMessage(BalanceChangeMessage message) {
        log.info("Received balance change message: {}", message);
        userBalanceService.changeBalance(message);
    }


}
