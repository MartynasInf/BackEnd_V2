package com.example.Street_manager.service;

import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.enums.OperationStatus;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.VoteAnswer;
import com.example.Street_manager.model.VotingOperation;
import com.example.Street_manager.repository.VotingOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingOperationService implements DataChecker<VotingOperation> {

    private final VoteAnswerService voteAnswerService;
    private final VotingOperationRepository votingOperationRepository;

    public List<VotingOperation> findAll(){
        return votingOperationRepository.findAll();
    }

    public void deleteFromDatabase(Long votingOperationId){
        votingOperationRepository.deleteById(votingOperationId);
    }

    public void createVotingOperation(VotingOperation votingOperation){
        votingOperation.setOperationStatus(OperationStatus.CREATED);
        votingOperation.setProgress(0);
        VotingOperation votingOp = votingOperationRepository.save(votingOperation);
        for (VoteAnswer voteAnswer : votingOperation.getVoteAnswers()) {
            voteAnswer.setVotingRequest(votingOp);
            voteAnswerService.saveVoteAnswer(voteAnswer);
        }
    }

    public void changeOperationStatus(VotingOperation votingOperationDetails){
        try {
            VotingOperation votingOperation = votingOperationRepository.findById(votingOperationDetails.getId())
                    .orElseThrow(() -> ResponseException.builder()
                            .message("Voting operation id was not found")
                            .build());
            votingOperation.setOperationStatus(votingOperationDetails.getOperationStatus());
            votingOperationRepository.save(votingOperation);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public Boolean checkIfIdExists(Long id) {
        if (votingOperationRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .message("Voting request id is non existent")
                    .build();
        }
    }
}
