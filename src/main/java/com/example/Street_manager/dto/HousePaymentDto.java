package com.example.Street_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HousePaymentDto {

    private Long id;
    private Double amount;
    private Boolean isPaid;

    private HouseDto house;

}
