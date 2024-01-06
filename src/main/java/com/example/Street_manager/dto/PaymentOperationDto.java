package com.example.Street_manager.dto;


import com.example.Street_manager.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOperationDto {

    private Long id;
    private String purpose;
    private LocalDate dueDate;
    private Double totalSum;
    private OperationStatus operationStatus;
    private String creator;
    private LocalDate creationDate;

    private List<HousePaymentDto> housePayments;

    private List<Integer> houseIds;

}
