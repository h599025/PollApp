package com.example.demo.Managers;

import com.example.demo.Models.AggregatedPollData;
import com.example.demo.Repositories.AggregatedPollDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsConsumer {

    @Autowired
    private AggregatedPollDataRepository aggregatedPollDataRepository;

    @RabbitListener(queues = "${rabbitmq.queue.pollAnalytics}")
    public void receiveAggregatedPollData(AggregatedPollData data) {
        // Save the aggregated data to MongoDB
        aggregatedPollDataRepository.save(data);
        System.out.println("Saved aggregated poll data to MongoDB: " + data);
    }
}
