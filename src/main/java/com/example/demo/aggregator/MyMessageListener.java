package com.example.demo.aggregator;

import com.example.demo.Models.AggregatedPollData;
import com.example.demo.Repositories.AggregatedPollDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyMessageListener {

    @Autowired
    private AggregatedPollDataRepository aggregatedPollDataRepository;

    public void handleMessage(AggregatedPollData data) {
        // Process and save the received data
        aggregatedPollDataRepository.save(data);
        System.out.println("Saved aggregated poll data to MongoDB: " + data);
    }
}
