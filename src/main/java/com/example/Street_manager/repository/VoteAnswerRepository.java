package com.example.Street_manager.repository;

import com.example.Street_manager.model.VoteAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteAnswerRepository extends JpaRepository<VoteAnswer, Long> {
}
