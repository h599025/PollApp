package com.example.demo.Models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    private Integer voteId;

    private String username;
    private Integer pollId;

    @ManyToOne
    @JoinColumn(name = "vote_option_id", nullable = false)
    private VoteOption voteOption;

    private Instant publishedAt;

    public Vote() {}
    public Vote(String username, Integer pollId, VoteOption voteOption, Instant publishedAt) {
        this.username = username;
        this.pollId = pollId;
        this.voteOption = voteOption;
        this.publishedAt = publishedAt;
    }

    public Integer getVoteId() { return voteId; }

    public void setVoteId(Integer voteId) { this.voteId = voteId; }

    public VoteOption getVoteOption() { return voteOption; }

    public void setVoteOption(VoteOption voteOption) { this.voteOption = voteOption; }

    public String getUsername() { return username; }

    public Integer getPollId() { return pollId; }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
