package com.example.Street_manager.controller;

import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.VotingOperation;
import com.example.Street_manager.service.UserService;
import com.example.Street_manager.service.VoteAnswerService;
import com.example.Street_manager.service.VotingOperationService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorised/polls")
@RequiredArgsConstructor
public class VotingOperationController {

    private final VotingOperationService votingOperationService;
    private final UserService userService;
    private final VoteAnswerService voteAnswerService;
    private final Logger logger = LogManager.getLogger(UserController.class);

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> getAllVotingRequests() {
        try {
            List<VotingOperation> votingRequests = votingOperationService.findAll();
            logger.info("All voting requests has been retrieved");
            return ResponseEntity.status(HttpStatus.OK).body(votingRequests);
        } catch (ResponseException e) {
            logger.error("An error occurred while retrieving users. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while retrieving users. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/changeVotingRequestOperationStatus")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> changeOperationStatus(@RequestBody VotingOperation votingOperation){
        try {
            votingOperationService.checkIfIdExists(votingOperation.getId());
            votingOperationService.changeOperationStatus(votingOperation);
            return ResponseEntity.status(HttpStatus.OK).body("Operation status has been changed successfully");
        } catch (ResponseException e){
            logger.error("An error occurred while changing voting operation status for voting operation ID = "
                    + votingOperation.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while changing payment operation status for payment operation ID = "
                    + votingOperation.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/vote/{voteAnswerId}/{userId}")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> voteForTheAnswer(@PathVariable Long voteAnswerId, @PathVariable Long userId){
        try {
            userService.checkIfIdExists(userId);
            voteAnswerService.checkIfIdExists(voteAnswerId);
            voteAnswerService.checkIfNotAlreadyVoted(voteAnswerId, userId);
            voteAnswerService.voteForTheAnswer(voteAnswerId, userId);
            return ResponseEntity.status(HttpStatus.OK).body("Vote has been accepted");
        } catch (ResponseException e){
            logger.error("An error occurred while voting for an answer "
                    + voteAnswerId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while voting for an answer "
                    + voteAnswerId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/createVoting")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> createVotingOperation(@RequestBody VotingOperation votingOp){
        try {
            System.out.println(votingOp);
            votingOperationService.createVotingOperation(votingOp);
            return ResponseEntity.status(HttpStatus.OK).body("Voting operation has been created has been accepted");
        } catch (ResponseException e){
            logger.error("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/deleteVoting/{votingOperationId}")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> deleteVotingOperation(@PathVariable Long votingOperationId){
        try {
            votingOperationService.checkIfIdExists(votingOperationId);
            votingOperationService.deleteFromDatabase(votingOperationId);
            return ResponseEntity.status(HttpStatus.OK).body("Voting operation has been deleted");
        } catch (ResponseException e){
            logger.error("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
