package com.example.demo.Repositories;

import com.example.demo.Models.AggregatedPollData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AggregatedPollDataRepository extends MongoRepository<AggregatedPollData, String> {
}
