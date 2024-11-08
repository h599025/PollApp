package com.example.demo.aggregator;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue pollQueue() {
        return new Queue("pollQueue", false);
    }
}