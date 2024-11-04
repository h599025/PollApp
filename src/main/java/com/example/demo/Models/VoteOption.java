package com.example.demo.Models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_options")
public class VoteOption {

    @Id
    private Integer voteOptionId;

    @ManyToOne
    @JoinColumn(name = "pollId", nullable = false)
    private Poll poll;

    private String caption;
    private Integer presentationOrder;

    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    public VoteOption() {}
    public VoteOption(Poll poll, String caption, Integer presentationOrder) {
        this.poll = poll;
        this.caption = caption;
        this.presentationOrder = presentationOrder;
    }

    public Integer getVoteOptionId() {
        return voteOptionId;
    }

    public void setVoteOptionId(Integer voteOptionId) {
        this.voteOptionId = voteOptionId;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getPresentationOrder() {
        return presentationOrder;
    }

    public void setPresentationOrder(Integer presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
}

