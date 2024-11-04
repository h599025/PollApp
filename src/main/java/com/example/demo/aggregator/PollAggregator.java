package com.example.demo.aggregator;

import com.example.demo.Models.Poll;
//import com.example.demo.Aggregator.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollAggregator {

    // @Autowired
    // private PollRepository pollRepository;

    public PollAggregator() {
        // Constructor logic here
    }

    public void aggregatePollData(String message) {
        // Parse the message and perform aggregation logic
        Poll poll = new Poll("a", "a", null, null);
        poll.setQuestion(message); // Example: set the question from the message
        // pollRepository.save(poll);
    }
}
