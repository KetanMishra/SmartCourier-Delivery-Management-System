package com.smartcourier.auth.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue userAuditQueue() {
        return new Queue("smartcourier.user.audit", true);
    }
}
