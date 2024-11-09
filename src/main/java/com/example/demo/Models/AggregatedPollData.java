package com.example.demo.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Document(collection = "pollAnalytics")
public class AggregatedPollData implements Serializable {

    @Id
    private String id;
    private Integer pollId;
    private String question;
    private Map<String, Long> optionVoteCounts;

    // No-arg constructor for Jackson
    public AggregatedPollData() {}

    public AggregatedPollData(Integer pollId, String question, Map<String, Long> optionVoteCounts) {
        this.pollId = pollId;
        this.question = question;
        this.optionVoteCounts = optionVoteCounts;
    }

    // Getters and setters

    public Integer getPollId() {
        return pollId;
    }

    public void setPollId(Integer pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, Long> getOptionVoteCounts() {
        return optionVoteCounts;
    }

    public void setOptionVoteCounts(Map<String, Long> optionVoteCounts) {
        this.optionVoteCounts = optionVoteCounts;
    }
}