package com.example.Street_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class HousePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private Boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "house_id")
    @JsonIgnore
    private House house;

    @ManyToOne
    @JoinColumn(name = "paymentRequest_id")
    @JsonIgnore
    private PaymentOperation paymentRequest;
}
