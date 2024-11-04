package com.example.demo.Managers;

import com.example.demo.Exceptions.*;
import com.example.demo.Models.*;
import com.example.demo.Repositories.PollRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Repositories.VoteOptionRepository;
import com.example.demo.Repositories.VoteRepository;
import com.example.demo.messaging.MessagePublisher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class PollManager {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private VoteOptionRepository voteOptionRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessagePublisher messagePublisher;

    // User CRUDs
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserNotFoundException("Username '" + user.getUsername() + "' is already taken.");
        }
        // Hash the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String username, User updatedUser) {
        User existingUser = getUser(username);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        return userRepository.save(existingUser);
    }

    public void deleteUser(String username) {
        User user = getUser(username);
        userRepository.delete(user);
    }


    // Poll CRUDs
    public Poll createPoll(Poll poll) {
        poll.setPollId(null); // Ensures a new poll ID
        return pollRepository.save(poll);
    }

    public Poll getPoll(Integer id) {
        return pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException("Poll not found."));
    }

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    public Poll updatePoll(Integer id, Poll updatedPoll) {
        Poll poll = getPoll(id);
        poll.setQuestion(updatedPoll.getQuestion());
        poll.setValidUntil(updatedPoll.getValidUntil());
        return pollRepository.save(poll);
    }

    public void deletePoll(Integer id) {
        pollRepository.deleteById(id);
    }


    // Vote CRUDs
    @Transactional
    public Vote voteOnOption(String username, Integer pollId, Integer voteOptionId, Instant publishedAt) {
        User user = getUser(username);
        Poll poll = getPoll(pollId);
        VoteOption voteOption = getVoteOption(voteOptionId);

        // Check if user has already voted on this poll
        if (voteRepository.existsByPollIdAndUsername(pollId, username)) {
            throw new IllegalStateException("User has already voted on this poll.");
        }

        Vote vote = new Vote(username, pollId, voteOption, publishedAt);
        vote = voteRepository.save(vote);

        publishAggregatedData(pollId);
        return vote;
    }

    public Vote getVote(Integer id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new VoteNotFoundException("Vote not found."));
    }

    public List<Vote> getAllVotesForPoll(Integer pollId) {
        return voteRepository.findByPollId(pollId);
    }

    public Vote updateVote(Integer id, Vote updatedVote) {
        Vote existingVote = getVote(id);
        existingVote.setVoteOption(updatedVote.getVoteOption());
        existingVote.setPublishedAt(updatedVote.getPublishedAt());
        return voteRepository.save(existingVote);
    }

    public void deleteVote(Integer id) {
        Vote existingVote = getVote(id);
        voteRepository.delete(existingVote);
    }


    // VoteOption CRUDs
    public VoteOption createVoteOption(Integer pollId, VoteOption voteOption) {
        Poll poll = getPoll(pollId);
        voteOption.setPoll(poll);
        return voteOptionRepository.save(voteOption);
    }

    public VoteOption getVoteOption(Integer id) {
        return voteOptionRepository.findById(id)
                .orElseThrow(() -> new VoteOptionNotFoundException("Vote option not found."));
    }

    public List<VoteOption> getAllVoteOptions(Integer pollId) {
        return voteOptionRepository.findByPoll_PollId(pollId);
    }

    public VoteOption updateVoteOption(Integer voteOptionId, VoteOption updatedVoteOption) {
        VoteOption existingVoteOption = getVoteOption(voteOptionId);
        existingVoteOption.setCaption(updatedVoteOption.getCaption());
        existingVoteOption.setPresentationOrder(updatedVoteOption.getPresentationOrder());
        return voteOptionRepository.save(existingVoteOption);
    }

    public void deleteVoteOption(Integer voteOptionId) {
        VoteOption voteOption = getVoteOption(voteOptionId);
        voteOptionRepository.delete(voteOption);
    }


    private void publishAggregatedData(Integer pollId) {
        Poll poll = getPoll(pollId);

        // Create a map to hold vote counts for each option
        Map<String, Long> optionVoteCounts = new HashMap<>();
        for (VoteOption option : poll.getVoteOptions()) {
            long voteCount = voteRepository.countByVoteOption_VoteOptionId(option.getVoteOptionId());
            optionVoteCounts.put(option.getCaption(), voteCount);
        }

        // Now pass the map as the third argument
        AggregatedPollData aggregatedData = new AggregatedPollData(poll.getPollId(), poll.getQuestion(), optionVoteCounts);

        // Publish to RabbitMQ
        messagePublisher.publish(aggregatedData);
    }
}
