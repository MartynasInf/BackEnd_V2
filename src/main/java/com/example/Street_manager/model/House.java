package com.example.Street_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String houseNumber;
    private String streetName;

    @OneToOne(mappedBy = "house")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "house", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<HousePayment> housePayments;

}
