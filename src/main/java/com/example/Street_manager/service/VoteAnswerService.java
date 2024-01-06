package com.example.Street_manager.service;

import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.User;
import com.example.Street_manager.model.VoteAnswer;
import com.example.Street_manager.repository.UserRepository;
import com.example.Street_manager.repository.VoteAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteAnswerService implements DataChecker<VoteAnswer> {

    private final VoteAnswerRepository voteAnswerRepository;
    private final UserRepository userRepository;

    public void voteForTheAnswer(Long voteAnswerId, Long userId){
        VoteAnswer voteAnswerInQuestion = voteAnswerRepository.findById(voteAnswerId).orElseThrow();
        User userToAdd = userRepository.findById(userId).orElseThrow();
        voteAnswerInQuestion.getVotedUsers().add(userToAdd);
        voteAnswerRepository.save(voteAnswerInQuestion);
    }

    public void checkIfNotAlreadyVoted(Long voteAnswerId, Long userId){
        VoteAnswer voteAnswerInQuestion = voteAnswerRepository.findById(voteAnswerId).orElseThrow();
        Set<Long> votedUserIds = voteAnswerInQuestion.getVotedUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        if (votedUserIds.contains(userId)) {
            throw ResponseException.builder()
                    .message("You already voted for this answer!")
                    .build();
        }

    }

    public void saveVoteAnswer(VoteAnswer voteAnswer){
        voteAnswerRepository.save(voteAnswer);
    }
    @Override
    public Boolean checkIfIdExists(Long id) {
        if (voteAnswerRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .message("Vote answer id is non existent")
                    .build();
        }
    }
}
