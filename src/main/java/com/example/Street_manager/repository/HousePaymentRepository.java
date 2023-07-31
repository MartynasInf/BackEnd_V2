package com.example.Street_manager.repository;

import com.example.Street_manager.model.HousePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HousePaymentRepository extends JpaRepository<HousePayment, Long> {
}
