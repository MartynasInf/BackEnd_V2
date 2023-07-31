package com.example.Street_manager.model;

import com.example.Street_manager.enums.PaymentOperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class PaymentOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String purpose;
    private LocalDate dueDate;
    private Double totalSum;
    private PaymentOperationStatus operationStatus;

    @OneToMany(mappedBy = "paymentRequest", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<HousePayment> housePayments;

}
