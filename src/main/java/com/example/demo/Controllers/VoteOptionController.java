package com.example.demo.Controllers;

import com.example.demo.Exceptions.PollNotFoundException;
import com.example.demo.Exceptions.VoteOptionNotFoundException;
import com.example.demo.Managers.PollManager;
import com.example.demo.Models.VoteOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/voteOptions")
public class VoteOptionController {

    @Autowired
    private PollManager repo;

    @PostMapping("/polls/{pollId}")
    public ResponseEntity<VoteOption> createVoteOption(@PathVariable Integer pollId,
            @RequestBody VoteOption voteOption) {
        System.out.println("POLL ID :: :: : " + pollId);
        System.out.println("VOTE OPTIONS :: :: : " + voteOption);
        try {
            System.out.println("POLL ID :: :: : " + pollId);
            System.out.println("VOTE OPTIONS :: :: : " + voteOption);
            VoteOption createdVO = repo.createVoteOption(pollId, voteOption);
            return new ResponseEntity<>(createdVO, HttpStatus.CREATED);
        } catch (PollNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteOption> getVoteOption(@PathVariable Integer id) {
        VoteOption voteOption = repo.getVoteOption(id);
        return new ResponseEntity<>(voteOption, HttpStatus.OK);
    }

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<List<VoteOption>> getAllVoteOptions(@PathVariable Integer pollId) {
        return new ResponseEntity<>(repo.getAllVoteOptions(pollId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoteOption> updateVoteOption(@PathVariable Integer id, @RequestBody VoteOption voteOption) {
        try {
            VoteOption updatedVoteOption = repo.updateVoteOption(id, voteOption);
            return new ResponseEntity<>(updatedVoteOption, HttpStatus.OK);
        } catch (VoteOptionNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoteOption(@PathVariable Integer id) {
        try {
            repo.deleteVoteOption(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (VoteOptionNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
