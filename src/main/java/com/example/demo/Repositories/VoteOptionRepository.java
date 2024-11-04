package com.example.demo.Repositories;

import com.example.demo.Models.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Integer> {
    List<VoteOption> findByPoll_PollId(Integer pollId);
}
