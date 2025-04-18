package org.greenflow.openorder.config;

import org.greenflow.common.model.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class RabbitMQConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("org.greenflow.*"));
        factory.setMessageConverter(converter);
        return factory;
    }

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
