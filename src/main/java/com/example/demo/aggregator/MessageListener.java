package com.example.demo.aggregator;

import com.example.demo.aggregator.PollAggregator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private PollAggregator pollAggregator;

    @RabbitListener(queues = "${rabbitmq.queue.pollAnalytics}")
    public void listen(String message) {
        try {
            logger.info("Received message: {}", message);
            pollAggregator.aggregatePollData(message);
            logger.info("Message processed successfully");
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
        }
    }
}
