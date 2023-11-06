package com.example.Street_manager.controller;

import com.example.Street_manager.dto.HousePaymentDto;
import com.example.Street_manager.service.HousePaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorised/housePayments")
@RequiredArgsConstructor
public class HousePaymentController {

    private final Logger logger = LogManager.getLogger(HousePaymentController.class);
    private final HousePaymentService housePaymentService;

    @PostMapping("/pay")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> payForThePaymentRequest(@RequestBody HousePaymentDto housePaymentDto) {
        try {
            housePaymentService.pay(housePaymentDto);
            logger.info("Payment for the house payment has been transferred successfully. House payment ID = " + housePaymentDto.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Payment was transferred successfully");
        } catch (Exception e) {
            logger.info("An unexpected error occurred while paying for the house payment ID = " + housePaymentDto.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
