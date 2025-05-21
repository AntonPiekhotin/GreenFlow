package org.greenflow.payment.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.PaymentCreationMessage;
import org.greenflow.payment.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.greenflow.common.model.constant.RabbitMQConstants.PAYMENT_CREATION_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    PaymentService paymentService;

    @RabbitListener(queues = PAYMENT_CREATION_QUEUE)
    public void consumeOrderAssignedMessage(PaymentCreationMessage message) {
        log.info("Received payment creation message: {}", message);
        paymentService.createPayment(message);
    }


}
