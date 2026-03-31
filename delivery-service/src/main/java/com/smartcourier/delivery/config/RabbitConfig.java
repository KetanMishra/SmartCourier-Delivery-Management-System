package com.smartcourier.delivery.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "smartcourier.delivery.exchange";
    public static final String ROUTING_KEY = "delivery.lifecycle";
    public static final String QUEUE = "smartcourier.delivery.lifecycle.queue";

    @Bean
    public DirectExchange deliveryExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue deliveryQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding deliveryBinding(Queue deliveryQueue, DirectExchange deliveryExchange) {
        return BindingBuilder.bind(deliveryQueue).to(deliveryExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
