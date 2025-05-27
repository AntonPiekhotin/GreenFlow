package org.greenflow.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.order.model.constant.OrderStatus;
import org.greenflow.order.output.event.RabbitMQProducer;
import org.greenflow.order.output.persistent.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersScheduler {

    private final OrderRepository orderRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Scheduled(fixedDelay = 10000) // Runs every 10 minutes
    public void scheduleOrderOpening() {
        log.info("Starting scheduled task to open orders");
        orderRepository.findAllCreatedOrders().forEach(
                order -> {
                    if (order.getStartDate().isBefore(LocalDate.now()) || order.getStartDate().isEqual(LocalDate.now())) {
                        rabbitMQProducer.sendOrderOpeningMessage(order, ""); //TODO
                        order.setStatus(OrderStatus.OPEN);
                        orderRepository.save(order);
                        log.info("Order {} is now open", order.getId());
                    }

                }
        );
    }
}
