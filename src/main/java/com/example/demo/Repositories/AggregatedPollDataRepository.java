package com.example.demo.Repositories;

import com.example.demo.Models.AggregatedPollData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AggregatedPollDataRepository extends MongoRepository<AggregatedPollData, String> {
    Optional<AggregatedPollData> findByPollId(Integer pollId);
}
