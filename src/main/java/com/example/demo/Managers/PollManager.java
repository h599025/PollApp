package com.example.demo.Managers;

import com.example.demo.Exceptions.*;
import com.example.demo.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PollManager {

    private Map<String, User> users = new HashMap<>();
    private Map<Integer, Poll> polls = new HashMap<>();
    private Map<Integer, Vote> votes = new HashMap<>();
    private Map<Integer, VoteOption> voteOptions = new HashMap<>();

    private Integer nextPollId = 0;
    private Integer nextVoteId = 0;
    private Integer nextVoteOptionId = 0;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        if (users.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }
        users.put(user.getUsername(), user);
        return user;
    }

    // User CRUDs
    public User createUser(User user) {
        if (users.containsKey(user.getUsername())) {
            throw new UserNotFoundException("Username '" + user.getUsername() + "' is already taken.");
        }
        // Hash the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        users.put(user.getUsername(), user);
        return user;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(String username, User updatedUser) {
        User existingUser = users.get(username);
        if (existingUser == null) {
            throw new UserNotFoundException("User not found.");
        }

        users.remove(username);

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        users.put(existingUser.getUsername(), existingUser);

        return existingUser;
    }

    public void deleteUser(String username) {
        users.remove(username);
    }


    // Poll CRUDs
    public Poll createPoll(Poll poll) {
        if (!users.containsKey(poll.getCreatorUsername())) {
            throw new InvalidUsername("Username '" + poll.getCreatorUsername() + "' does not exist.");
        }

        poll.setPollId(nextPollId++);
        polls.put(poll.getPollId(), poll);

        if (poll.getVoteOptions() != null && !poll.getVoteOptions().isEmpty()) {
            List<VoteOption> optionsCopy = new ArrayList<>(poll.getVoteOptions());
            for (VoteOption option : optionsCopy) {
                option.setPollId(poll.getPollId());
                createVoteOption(option);
            }
        }

        return poll;
    }

    public Poll getPoll(Integer id) {
        Poll poll = polls.get(id);
        if (poll == null) {
            throw new PollNotFoundException("Poll not found.");
        }
        return poll;
    }

    public List<Poll> getAllPolls() {
        return new ArrayList<>(polls.values());
    }

    public Poll updatePoll(Integer id, Poll updatedPoll) {
        Poll existingPoll = polls.get(id);
        if (existingPoll == null) {
            throw new PollNotFoundException("Poll not found.");
        }

        existingPoll.setQuestion(updatedPoll.getQuestion());
        existingPoll.setValidUntil(updatedPoll.getValidUntil());

        polls.put(id, existingPoll);
        return existingPoll;
    }

    public void deletePoll(Integer id) {
        if (!polls.containsKey(id)) {
            throw new PollNotFoundException("Poll not found.");
        }

        polls.remove(id);

        List<Integer> voteOptionIds = voteOptions.values().stream()
                .filter(voteOption -> voteOption.getPollId().equals(id))
                .map(VoteOption::getVoteOptionId)
                .collect(Collectors.toList());

        for (Integer voteOptionId : voteOptionIds) {
            voteOptions.remove(voteOptionId);

            votes.entrySet().removeIf(entry -> entry.getValue().getVoteOptionId().equals(voteOptionId));
        }
    }


    // Vote CRUDs
    public Vote voteOnOption(String username, Integer pollId, Integer voteOptionId, Instant publishedAt) {
        User user = users.get(username);
        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        Poll poll = polls.get(pollId);
        if (poll == null) {
            throw new PollNotFoundException("Poll not found.");
        }

        VoteOption voteOption = voteOptions.get(voteOptionId);
        if (voteOption == null || !poll.getVoteOptions().contains(voteOption)) {
            throw new VoteOptionNotFoundException("Vote option not found for this poll.");
        }

        for (Vote existingVote : votes.values()) {
            if (existingVote.getPollId().equals(pollId) && existingVote.getUsername().equals(user.getUsername())) {
                throw new IllegalStateException("User has already voted on this poll.");
            }
        }

        Vote newVote = new Vote(username, pollId, voteOptionId, publishedAt);
        newVote.setVoteId(nextVoteId++);

        voteOption.getVotes().add(newVote);
        votes.put(newVote.getVoteId(), newVote);

        return newVote;
    }

    public Vote getVote(Integer id) {
        return votes.get(id);
    }

    public List<Vote> getAllVotes() {
        return new ArrayList<>(votes.values());
    }

    public Vote updateVote(Integer id, Vote updatedVote) {
        Vote existingVote = votes.get(id);
        if (existingVote == null) {
            throw new VoteNotFoundException("Vote not found.");
        }

        if (!existingVote.getVoteOptionId().equals(updatedVote.getVoteOptionId()) &&
                existingVote.getPollId().equals(updatedVote.getPollId())) {

            removeVoteFromOption(existingVote);

            VoteOption newOption = voteOptions.get(updatedVote.getVoteOptionId());
            if (newOption == null) {
                throw new VoteOptionNotFoundException("Vote option not found.");
            }
            existingVote.setVoteOptionId(updatedVote.getVoteOptionId());
            existingVote.setPublishedAt(updatedVote.getPublishedAt());
            newOption.getVotes().add(existingVote);
        } else {
            existingVote.setPublishedAt(updatedVote.getPublishedAt());
        }

        votes.put(id, existingVote);
        return existingVote;
    }

    public void deleteVote(Integer id) {
        Vote existingVote = votes.get(id);
        if (existingVote != null) {
            removeVoteFromOption(existingVote);
            votes.remove(id);
        }
    }

    public void removeVoteFromOption(Vote existingVote) {
        VoteOption currentOption = voteOptions.get(existingVote.getVoteOptionId());
        if (currentOption != null) {
            currentOption.getVotes().removeIf(vote -> vote.getVoteId().equals(existingVote.getVoteId()));
        }
    }


    // VoteOption CRUDs
    public VoteOption createVoteOption(VoteOption voteOption) {
        Poll poll = polls.get(voteOption.getPollId());
        if (poll == null) {
            throw new PollNotFoundException("Poll not found with ID: " + voteOption.getPollId());
        }

        voteOption.setVoteOptionId(nextVoteOptionId++);
        voteOptions.put(voteOption.getVoteOptionId(), voteOption);

        return voteOption;
    }

    public VoteOption getVoteOption(Integer id) { return voteOptions.get(id); }

    public List<VoteOption> getAllVoteOptions() {
        return new ArrayList<>(voteOptions.values());
    }

    public VoteOption updateVoteOption(Integer voteOptionId, VoteOption updatedVoteOption) {
        VoteOption existingVoteOption = voteOptions.get(voteOptionId);
        if (existingVoteOption == null) {
            throw new VoteOptionNotFoundException("Vote option not found.");
        }

        existingVoteOption.setCaption(updatedVoteOption.getCaption());
        existingVoteOption.setPresentationOrder(updatedVoteOption.getPresentationOrder());

        voteOptions.put(voteOptionId, existingVoteOption);
        return existingVoteOption;
    }

    public void deleteVoteOption(Integer voteOptionId) {
        VoteOption voteOption = voteOptions.remove(voteOptionId);
        if (voteOption == null) {
            throw new VoteOptionNotFoundException("Vote option not found.");
        }

        votes.entrySet().removeIf(entry -> entry.getValue().getVoteOptionId().equals(voteOptionId));

        Poll parentPoll = polls.get(voteOption.getPollId());
        if (parentPoll != null) {
            parentPoll.getVoteOptions().removeIf(vo -> vo.getVoteOptionId().equals(voteOptionId));
        }
    }
}
