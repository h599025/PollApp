package com.example.demo.Repositories;

import com.example.demo.Models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findByPollId(Integer pollId);
    boolean existsByPollIdAndUsername(Integer pollId, String username);
    long countByVoteOption_VoteOptionId(Integer voteOptionId);
}
