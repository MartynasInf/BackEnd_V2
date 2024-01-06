package com.example.Street_manager.model;

import com.example.Street_manager.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class VotingOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @FutureOrPresent
    private LocalDate finishDate;
    @NotNull
    private Integer progress;
    @NotNull
    private OperationStatus operationStatus;
    @NotNull
    private String creator;

    @OneToMany(mappedBy = "votingRequest", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<VoteAnswer> voteAnswers;

    @ManyToMany
    @JoinTable(name = "voting_users",
            joinColumns = @JoinColumn(name = "votingRequest_id"),
            inverseJoinColumns = @JoinColumn(name = "votingUser"))
    private List<User> votingUsers;
}
