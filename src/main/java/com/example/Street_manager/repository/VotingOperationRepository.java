package com.example.Street_manager.repository;

import com.example.Street_manager.model.VotingOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingOperationRepository extends JpaRepository<VotingOperation, Long> {
}
