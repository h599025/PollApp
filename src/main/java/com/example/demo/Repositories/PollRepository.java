package com.example.demo.Repositories;

import com.example.demo.Models.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Integer> {
}
