package com.example.Street_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class VoteAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String answer;

    @ManyToOne
    @JoinColumn(name = "votingRequest_id")
    @JsonIgnore
    private VotingOperation votingRequest;
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<User> votedUsers;
}
