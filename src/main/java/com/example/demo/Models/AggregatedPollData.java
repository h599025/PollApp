package com.example.demo.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "pollAnalytics")
public class AggregatedPollData {

    @Id
    private String id;

    private Integer pollId;
    private String question;
    private Map<String, Long> optionVoteCounts; // Option captions with vote counts

    public AggregatedPollData(Integer pollId, String question, Map<String, Long> optionVoteCounts) {
        this.pollId = pollId;
        this.question = question;
        this.optionVoteCounts = optionVoteCounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
