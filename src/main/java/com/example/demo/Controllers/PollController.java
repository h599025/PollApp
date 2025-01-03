package com.example.demo.Controllers;

import com.example.demo.Exceptions.PollNotFoundException;
import com.example.demo.Managers.PollManager;
import com.example.demo.Models.Poll;
import com.example.demo.Models.Vote;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/polls")
public class PollController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PollManager repo;

    @PostMapping()
    public ResponseEntity<Poll> createPoll(@RequestBody Poll poll) {
        Poll createdPoll = repo.createPoll(poll);
        List<Poll> userPollList = repo.getAllPollsByCreator(poll.getCreatorUsername());
        int pollId = userPollList.get(userPollList.size() - 1).getPollId();
        createdPoll.setPollId(pollId);
        // rabbitTemplate.convertAndSend("pollQueue", poll.getQuestion());
        return new ResponseEntity<>(createdPoll, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPoll(@PathVariable Integer id) {
        Poll poll = repo.getPoll(id);
        if (poll == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Poll>> getAllPolls() {
        return new ResponseEntity<>(repo.getAllPolls(), HttpStatus.OK);
    }

    @GetMapping("user/{creatorName}")
    public ResponseEntity<List<Poll>> getAllPollsByCreator(@PathVariable String creatorName) {
        return new ResponseEntity<>(repo.getAllPollsByCreator(creatorName), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable Integer id, @RequestBody Poll poll) {
        System.out.println("POLL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+poll);
        System.out.println("Valid until: " + poll.getValidUntil());
        try {
            Poll updatedPoll = repo.updatePoll(id, poll);
            return new ResponseEntity<>(updatedPoll, HttpStatus.OK);
        } catch (PollNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Integer id) {
        try {
            repo.deletePoll(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (PollNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
