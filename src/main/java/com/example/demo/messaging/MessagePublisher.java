package com.example.demo.messaging;

import com.example.demo.Models.AggregatedPollData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // The name of the queue to which messages will be sent
    @Value("${rabbitmq.queue.pollAnalytics}")
    private String pollAnalyticsQueue;

    public void publish(AggregatedPollData data) {
        // Convert and send the AggregatedPollData object to the specified queue
        rabbitTemplate.convertAndSend(pollAnalyticsQueue, data);
        System.out.println("Published message to RabbitMQ: " + data);
    }
}
