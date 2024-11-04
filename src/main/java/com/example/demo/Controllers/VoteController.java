package com.example.demo.Controllers;

import com.example.demo.Exceptions.PollNotFoundException;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Exceptions.VoteNotFoundException;
import com.example.demo.Exceptions.VoteOptionNotFoundException;
import com.example.demo.Managers.PollManager;
import com.example.demo.Models.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/polls/{pollId}/voteOptions/{voteOptionId}/votes")
public class VoteController {

    @Autowired
    private PollManager repo;

    @PostMapping
    public ResponseEntity<Vote> voteOnOption(@PathVariable String username, @PathVariable Integer pollId,
                                             @PathVariable Integer voteOptionId, @RequestBody Vote vote) {
        try {
            Vote newVote = repo.voteOnOption(username, pollId, voteOptionId, vote.getPublishedAt());
            return new ResponseEntity<>(newVote, HttpStatus.CREATED);
        } catch (UserNotFoundException | PollNotFoundException | VoteOptionNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVote(@PathVariable Integer id) {
        Vote vote = repo.getVote(id);
        return new ResponseEntity<>(vote, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAllVotesForPoll(@PathVariable Integer pollId) {
        return new ResponseEntity<>(repo.getAllVotesForPoll(pollId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vote> updateVote(@PathVariable Integer id, @RequestBody Vote vote) {
        try {
            Vote updatedVote = repo.updateVote(id, vote);
            return new ResponseEntity<>(updatedVote, HttpStatus.OK);
        } catch (VoteNotFoundException | VoteOptionNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Integer id) {
        repo.deleteVote(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
