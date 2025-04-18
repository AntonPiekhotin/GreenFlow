package org.greenflow.openorder.config;

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

    @Bean(name = "orderOpeningQueue")
    public Queue orderOpeningQueue() {
        return new Queue(RabbitMQConstants.ORDER_OPENING_QUEUE, true);
    }

    @Bean
    public Binding orderOpeningBinding(@Qualifier("orderOpeningQueue") Queue queue,
                                       TopicExchange orderExchange) {
        return BindingBuilder.bind(queue).to(orderExchange).with("order.opening.#");
    }


}
