package com.example.Street_manager.model;

import com.example.Street_manager.enums.PaymentOperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
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

    @NotNull
    @NotBlank
    private String purpose;
    @NotNull
    @FutureOrPresent
    private LocalDate dueDate;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double totalSum;
    @NotNull
    private PaymentOperationStatus operationStatus;
    @NotNull
    private String creator;
    @NotNull
    private LocalDate creationDate;


    @OneToMany(mappedBy = "paymentRequest", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<HousePayment> housePayments;

}
