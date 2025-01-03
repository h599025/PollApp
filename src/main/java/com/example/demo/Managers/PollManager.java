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
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }
        // Hash the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        } else {
            throw new UserNotFoundException("User not found.");
        }
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
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Testng
        return userRepository.save(existingUser);
    }

    public void deleteUser(String username) {
        User user = getUser(username);
        userRepository.delete(user);
    }

    // Poll CRUDs
    public Poll createPoll(Poll poll) {
        // poll.setPollId(null); // Ensures a new poll ID
        return pollRepository.save(poll);
    }

    public List<Poll> getAllPollsByCreator(String creatorUsername) {
        return pollRepository.findAllPollsByCreatorUsername(creatorUsername);
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

    @Transactional
    public Vote voteOnOption(String username, Integer pollId, Integer voteOptionId, Instant publishedAt) {
        User user = getUser(username); // Ensure user exists
        Poll poll = getPoll(pollId); // Ensure poll exists
        VoteOption voteOption = getVoteOption(voteOptionId); // Ensure vote option exists

        // Validate that the voteOption belongs to the poll
        if (!voteOption.getPoll().getPollId().equals(pollId)) {
            throw new IllegalStateException("VoteOption does not belong to the specified Poll.");
        }

        // Check if user has already voted on this poll
        if (voteRepository.existsByPollIdAndUsername(pollId, username)) {
            throw new IllegalStateException("User has already voted on this poll.");
        }

        // Create the vote and save it
        Vote vote = new Vote(username, pollId, voteOption, publishedAt);
        vote = voteRepository.save(vote);
        
        System.out.println("BEFORE ERROR");
        publishAggregatedData(pollId); // Aggregate data for analytics
        System.out.println("AFTER ERROR");
        return vote;
    }

    public Vote getVote(Integer id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new VoteNotFoundException("Vote not found."));
    }

    public List<Vote> getAllVotesForPoll(Integer pollId) {
        return voteRepository.findByPollId(pollId);
    }

    public Vote updateVote(Integer id, Integer newId, Vote updatedVote) {
        Vote existingVote = getVote(id);

        // Log the initial state of the vote
        System.out.println("Current Vote Option ID: " + existingVote.getVoteOption().getVoteOptionId());

        // Set the new VoteOption
        VoteOption newVoteOption = voteOptionRepository.findById(newId)
                .orElseThrow(() -> new VoteOptionNotFoundException("Vote option not found."));
        existingVote.setVoteOption(newVoteOption);

        // Update additional fields if provided
        if (updatedVote.getPublishedAt() != null) {
            existingVote.setPublishedAt(updatedVote.getPublishedAt());
        }

        // Save the updated vote in H2
        Vote savedVote = voteRepository.save(existingVote);

        // Log the updated state of the vote
        System.out.println(
                "Updated Vote with ID " + id + " to VoteOption " + savedVote.getVoteOption().getVoteOptionId());

        // Publish updated aggregated data to RabbitMQ for MongoDB update
        publishAggregatedData(savedVote.getPollId());

        return savedVote;
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
        Poll poll = getPoll(pollId); // Fetch the latest poll data

        // Create a map to hold updated vote counts for each option
        Map<String, Long> optionVoteCounts = new HashMap<>();
        for (VoteOption option : poll.getVoteOptions()) {
            // Use the repository to fetch the exact count from the database for each
            // VoteOption
            long voteCount = voteRepository.countByVoteOption_VoteOptionId(option.getVoteOptionId());
            optionVoteCounts.put(option.getCaption(), voteCount);
        }

        System.out.println("BEEEEEEEEEEEEEEFORE AGGREGATEPOLLDATA CLASS");
        // Construct AggregatedPollData with the updated counts
        AggregatedPollData aggregatedData = new AggregatedPollData(poll.getPollId(), poll.getQuestion(),
                optionVoteCounts);

        // Publish the updated data to RabbitMQ
        messagePublisher.publish(aggregatedData);
    }
}
