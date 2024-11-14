package com.example.demo.Repositories;

import com.example.demo.Models.Poll;
import com.example.demo.Models.Vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Integer> {
    List<Poll> findAllPollsByCreatorUsername(String creatorUsername);
}
