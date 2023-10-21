package com.example.Street_manager.controller;

import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.PaymentOperation;
import com.example.Street_manager.service.HouseService;
import com.example.Street_manager.service.PaymentOperationService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/authorised/payments")
@RequiredArgsConstructor
public class PaymentOperationController {
    private final Logger logger = LogManager.getLogger(PaymentOperationController.class);
    private final PaymentOperationService paymentOperationService;
    private final HouseService houseService;

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> findAll() {
        try {
            List<PaymentOperationDto> paymentOperations = paymentOperationService.getAll();
            logger.info("Payment operations has been retrieved successfully");
            return ResponseEntity.status(HttpStatus.OK).body(paymentOperations);
        } catch (Exception e) {
            logger.error("An error has occurred while retrieving payment operations. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/newPaymentRequest")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> createNewPaymentOperation(@RequestBody PaymentOperationDto paymentOperationDto) {
        try {
            logger.info("Creating new payment operation");
            paymentOperationService.validateIfHousesAreSelected(paymentOperationDto);
            paymentOperationDto.getHouseIds().forEach(id -> houseService.checkIfIdExists(id.longValue()));
            paymentOperationDto.getHouseIds().forEach(id -> paymentOperationService.validatesSelectedHousesIsWithOwners(id.longValue()));
            paymentOperationService.validatePaymentOperationTotalSum(paymentOperationDto);
            paymentOperationService.validatePaymentOperationDueDate(paymentOperationDto);
            PaymentOperation paymentOperation = paymentOperationService.createNewPaymentOperation(paymentOperationDto);
            logger.info("Payment operation has been created successfully. Operation ID = " + paymentOperation.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Payment operation has been created successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while creating new payment operation. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletePaymentRequest/{paymentId}")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> deletePaymentOperation(@PathVariable Long paymentId) {
        try {
            logger.info("Deleting payment operation ID = " + paymentId);
            paymentOperationService.checkIfIdExists(paymentId);
            paymentOperationService.deletePaymentOperation(paymentId);
            logger.info("Payment operation with ID " + paymentId + " was deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body("Payment operation was deleted successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while deleting payment operation with ID = "
                    + paymentId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/changePaymentRequestStatus")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> changePaymentOperationStatus(@RequestBody PaymentOperationDto paymentRequest) {
        try {
            logger.info("Changing payment operation status");
            paymentOperationService.validatePaymentOperationId(paymentRequest.getId());
            paymentOperationService.changePaymentOperationStatus(paymentRequest);
            logger.info("Payment operation status has been changed successfully. Payment operation ID = " + paymentRequest.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Operation status has been changed successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while changing payment operation status for payment operation ID = "
                    + paymentRequest.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while changing payment operation status for payment operation ID = "
                    + paymentRequest.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/updatePaymentOperationDetails")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> updatePaymentOperation(@RequestBody PaymentOperationDto paymentOperationDto){
        try {
            logger.info("Changing payment operation details");
            paymentOperationService.validatePaymentOperationId(paymentOperationDto.getId());
            paymentOperationService.checkIfIdExists(paymentOperationDto.getId());
            paymentOperationService.changePaymentOperationDetails(paymentOperationDto);
            return ResponseEntity.status(HttpStatus.OK).body("Payment operation details was updated successfully");
        } catch (ResponseException e){
            logger.error("An error occurred while updating payment operation for payment operation ID = "
                    + paymentOperationDto.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
