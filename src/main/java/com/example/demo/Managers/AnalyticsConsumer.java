package com.example.demo.Managers;

import com.example.demo.Models.AggregatedPollData;
import com.example.demo.Repositories.AggregatedPollDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnalyticsConsumer {

    @Autowired
    private AggregatedPollDataRepository aggregatedPollDataRepository;

    @RabbitListener(queues = "${rabbitmq.queue.pollAnalytics}")
    public void receiveAggregatedPollData(AggregatedPollData data) {
        Optional<AggregatedPollData> existingData = aggregatedPollDataRepository.findByPollId(data.getPollId());

        if (existingData.isPresent()) {
            AggregatedPollData existingDoc = existingData.get();
            existingDoc.setOptionVoteCounts(data.getOptionVoteCounts());  // update vote counts
            aggregatedPollDataRepository.save(existingDoc);  // save the updated document
        } else {
            aggregatedPollDataRepository.save(data);  // create new document if it doesn't exist
        }

        System.out.println("Upserted aggregated poll data to MongoDB: " + data);
    }
}
