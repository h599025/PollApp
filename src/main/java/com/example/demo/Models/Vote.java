package com.example.demo.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId;

    private String username;
    private Integer pollId;

    @ManyToOne
    @JoinColumn(name = "vote_option_id", nullable = false)
    @JsonIgnore
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

    public void setUsername(String username) { this.username = username; }

    public Integer getPollId() { return pollId; }

    public void setPollId(Integer pollId) { this.pollId = pollId; }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
