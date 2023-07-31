package com.example.Street_manager.controller;

import com.example.Street_manager.dto.HousePaymentDto;
import com.example.Street_manager.service.HousePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorised/housePayments")
@RequiredArgsConstructor
public class HousePaymentController {

    private final HousePaymentService housePaymentService;

    @PostMapping("/pay")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> payForThePaymentRequest(@RequestBody HousePaymentDto housePaymentDto) {
        try {
            housePaymentService.payThePayment(housePaymentDto);
            return ResponseEntity.status(HttpStatus.OK).body("Payment was paid successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
