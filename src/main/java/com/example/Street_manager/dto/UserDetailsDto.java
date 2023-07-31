package com.example.Street_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long house;
    private String phoneNumber;
    private String bankAccount;
    private Boolean enabled;
}
