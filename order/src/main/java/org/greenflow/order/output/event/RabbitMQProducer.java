package org.greenflow.order.output.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.BalanceChangeMessage;
import org.greenflow.common.model.dto.event.OrderDeletionMessage;
import org.greenflow.common.model.dto.event.OrderOpeningMessage;
import org.greenflow.common.model.dto.event.OrderUpdatingMessage;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.order.model.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    public static final String FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ = "Failed to send message to RabbitMQ";
    private final RabbitTemplate rabbitTemplate;

    // worker wage is 90% of the order total price
    private static final BigDecimal WORKER_WAGE_MULTIPLIER = BigDecimal.valueOf(0.9);

    public void sendOrderOpeningMessage(Order order, String clientEmail) {
        if (!LocalDateTime.now().isAfter(order.getStartDate().atStartOfDay())) {
            return;
        }
        try {
            var orderOpeningMessage = OrderOpeningMessage.builder()
                    .orderId(order.getId())
                    .clientId(order.getClientId())
                    .clientEmail(clientEmail)
                    .longitude(order.getLongitude())
                    .latitude(order.getLatitude())
                    .description(order.getDescription())
                    .wage(order.getTotalPrice().multiply(WORKER_WAGE_MULTIPLIER))
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_EXCHANGE, RabbitMQConstants.ORDER_OPENING_QUEUE,
                    orderOpeningMessage);
            log.info("Order opening message sent to RabbitMQ: {}", orderOpeningMessage.getOrderId());
        } catch (Exception e) {
            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
        }
    }

    public void sendOrderUpdatingMessage(Order order) {
        try {
            var orderUpdatingMessage = OrderUpdatingMessage.builder()
                    .orderId(order.getId())
                    .startDate(order.getStartDate())
                    .description(order.getDescription())
                    .wage(order.getOrderItems().stream()
                            .map(item -> item.getService().getPricePerUnit()
                                    .multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add).multiply(WORKER_WAGE_MULTIPLIER))
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_EXCHANGE, RabbitMQConstants.ORDER_UPDATING_QUEUE,
                    orderUpdatingMessage);
            log.info("Order updating message sent to RabbitMQ: {}", order.getId());
        } catch (Exception e) {
            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
        }
    }

    public void sendOrderDeletionMessage(Order order) {
        try {
            var orderDeletionMessage = OrderDeletionMessage.builder()
                    .orderId(order.getId())
                    .build();
            rabbitTemplate.convertAndSend(RabbitMQConstants.ORDER_EXCHANGE, RabbitMQConstants.ORDER_DELETION_QUEUE,
                    orderDeletionMessage);
            log.info("Order deletion message sent to RabbitMQ: {}", orderDeletionMessage.getOrderId());
        } catch (Exception e) {
            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
        }
    }

    /**
     * Sends a message to RabbitMQ to change client and worker balances after completed order.
     *
     * @param order The order that has been completed.
     */
    public void sendBalanceUpdateMessagesForCompletedOrder(Order order) {
        try {
            //client balance
            rabbitTemplate.convertAndSend(RabbitMQConstants.BALANCE_EXCHANGE, RabbitMQConstants.BALANCE_CHANGE_QUEUE,
                    BalanceChangeMessage.builder()
                            .userId(order.getClientId())
                            .amount(order.getTotalPrice().negate())
                            .build()
            );
            log.info("Balance change message sent to RabbitMQ for client: {}", order.getClientId());

            //worker balance
            rabbitTemplate.convertAndSend(RabbitMQConstants.BALANCE_EXCHANGE, RabbitMQConstants.BALANCE_CHANGE_QUEUE,
                    BalanceChangeMessage.builder()
                            .userId(order.getWorkerId())
                            .amount(order.getTotalPrice().multiply(WORKER_WAGE_MULTIPLIER))
                            .build()
            );
            log.info("Balance change message sent to RabbitMQ for worker: {}", order.getWorkerId());
        } catch (Exception e) {
            log.error("Failed to send order completed message to RabbitMQ", e);
            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
        }
    }

//    public void sendOrderCompletedNotification(Order order) {
//        try {
//            // send email to client
//
//            // send email to worker
//        } catch (Exception e) {
//            log.error("Failed to send order completed notification to RabbitMQ", e);
//            throw new GreenFlowException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    FAILED_TO_SEND_MESSAGE_TO_RABBIT_MQ, e);
//        }
//    }
}