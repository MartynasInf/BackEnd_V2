package com.example.Street_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HouseDto {

    private Long id;
    private String houseNumber;
    private String streetName;
    private UserDetailsDto user;
    private Double plotArea;
}
