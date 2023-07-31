package com.example.Street_manager.repository;

import com.example.Street_manager.model.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, Long> {
}
