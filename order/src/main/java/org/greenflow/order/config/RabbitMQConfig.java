package org.greenflow.order.config;

import org.greenflow.common.model.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(RabbitMQConstants.ORDER_EXCHANGE);
    }

    @Bean(name = "orderCreationQueue")
    public Queue orderCreationQueue() {
        return new Queue(RabbitMQConstants.ORDER_CREATION_QUEUE, true);
    }

    @Bean
    public Binding orderCreationBinding(@Qualifier("orderCreationQueue") Queue orderQueue,
                                        TopicExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with("order.creation.#");
    }

    @Bean(name = "orderDeletionQueue")
    public Queue orderDeletionQueue() {
        return new Queue(RabbitMQConstants.ORDER_DELETION_QUEUE, true);
    }

    @Bean
    public Binding orderDeletingBinding(@Qualifier("orderDeletionQueue") Queue orderQueue,
                                        TopicExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with("order.deletion.#");
    }
}
