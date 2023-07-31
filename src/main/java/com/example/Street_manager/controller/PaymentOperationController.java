package com.example.Street_manager.controller;


import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.service.HouseService;
import com.example.Street_manager.service.PaymentOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/authorised/payments")
@RequiredArgsConstructor
public class PaymentOperationController {

    private final PaymentOperationService paymentOperationService;
    private final HouseService houseService;

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(paymentOperationService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/newPaymentRequest")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> createNewPaymentOperation(@RequestBody PaymentOperationDto paymentOperationDto) {
        try {
            paymentOperationService.validateIfHousesAreSelected(paymentOperationDto);
            paymentOperationDto.getHouseIds().forEach(id -> houseService.checkIfIdExists(id.longValue()));
            paymentOperationDto.getHouseIds().forEach(id -> paymentOperationService.validatesSelectedHousesIsWithOwners(id.longValue()));
            paymentOperationService.validatePaymentOperationTotalSum(paymentOperationDto);
            paymentOperationService.validatePaymentOperationDueDate(paymentOperationDto);
            paymentOperationService.createNewPaymentOperation(paymentOperationDto);
            return ResponseEntity.status(HttpStatus.OK).body("Payment operation has been created successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletePaymentRequest/{paymentId}")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> deletePaymentOperation(@PathVariable Long paymentId) {
        try {
            paymentOperationService.checkIfIdExists(paymentId);
            paymentOperationService.deletePaymentOperation(paymentId);
            return ResponseEntity.status(HttpStatus.OK).body("Payment operation deleted successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/changePaymentRequestStatus")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> changePaymentOperationStatus(@RequestBody PaymentOperationDto paymentRequest) {
        try {
            paymentOperationService.validatePaymentOperationId(paymentRequest);
            paymentOperationService.changePaymentOperationStatus(paymentRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Operation status has been changed successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
