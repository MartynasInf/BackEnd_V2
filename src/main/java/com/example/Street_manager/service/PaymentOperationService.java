package com.example.Street_manager.service;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.dto.HousePaymentDto;
import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.enums.PaymentOperationStatus;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.HousePayment;
import com.example.Street_manager.model.PaymentOperation;
import com.example.Street_manager.repository.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentOperationService implements DataChecker<PaymentOperation> {

    private final PaymentOperationRepository paymentOperationRepository;
    private final HousePaymentService housePaymentService;
    private final HouseService houseService;

    public List<PaymentOperationDto> getAll() {
        List<PaymentOperation> payments = paymentOperationRepository.findAll();
        return mapToPaymentOperationDto(payments);
    }

    public void createNewPaymentOperation(PaymentOperationDto paymentOperationDto) {
        try {
            PaymentOperation newPayment = PaymentOperation.builder()
                    .purpose(paymentOperationDto.getPurpose())
                    .dueDate(paymentOperationDto.getDueDate())
                    .totalSum(paymentOperationDto.getTotalSum())
                    .operationStatus(PaymentOperationStatus.CREATION)
                    .build();
            newPayment = paymentOperationRepository.save(newPayment);
            newPayment.setHousePayments(housePaymentService.createHousePayments(paymentOperationDto, newPayment.getId()));
            paymentOperationRepository.save(newPayment);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void deletePaymentOperation(Long paymentId) {
        try {
            paymentOperationRepository.deleteById(paymentId);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void changePaymentOperationStatus(PaymentOperationDto paymentOperationDto) {
        try {
            PaymentOperation paymentOperation = paymentOperationRepository.findById(paymentOperationDto.getId())
                    .orElseThrow(() -> ResponseException.builder()
                            .message("Payment operation id was not found")
                            .build());
            paymentOperation.setOperationStatus(paymentOperationDto.getOperationStatus());
            paymentOperationRepository.save(paymentOperation);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private List<PaymentOperationDto> mapToPaymentOperationDto(List<PaymentOperation> payments) {
        try {
            List<PaymentOperationDto> paymentOperationsDto = new ArrayList<>();
            for (PaymentOperation payment : payments) {
                PaymentOperationDto paymentOperationDto = PaymentOperationDto.builder()
                        .id(payment.getId())
                        .purpose(payment.getPurpose())
                        .dueDate(payment.getDueDate())
                        .totalSum(payment.getTotalSum())
                        .operationStatus(payment.getOperationStatus())
                        .housePayments(mapToHousePaymentDto(payment))
                        .build();
                paymentOperationsDto.add(paymentOperationDto);
            }
            return paymentOperationsDto;
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private List<HousePaymentDto> mapToHousePaymentDto(PaymentOperation payment) {
        try {
            List<HousePaymentDto> housePaymentsDto = new ArrayList<>();
            for (HousePayment housePayment : payment.getHousePayments()) {
                HousePaymentDto housePaymentDto = HousePaymentDto.builder()
                        .id(housePayment.getId())
                        .amount(housePayment.getAmount())
                        .isPaid(housePayment.getIsPaid())
                        .house(mapToHouseDto(housePayment))
                        .build();
                housePaymentsDto.add(housePaymentDto);
            }
            return housePaymentsDto;
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private UserDetailsDto mapToUserDetailsDto(HousePayment housePayment) {
        try {
            return UserDetailsDto.builder()
                    .id(housePayment.getHouse().getUser().getId())
                    .firstName(housePayment.getHouse().getUser().getFirstName())
                    .lastName(housePayment.getHouse().getUser().getLastName())
                    .email(housePayment.getHouse().getUser().getEmail())
                    .house(housePayment.getHouse().getId())
                    .phoneNumber(housePayment.getHouse().getUser().getPhoneNumber())
                    .bankAccount(housePayment.getHouse().getUser().getBankAccount())
                    .enabled(housePayment.getHouse().getUser().isEnabled())
                    .build();
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private HouseDto mapToHouseDto(HousePayment housePayment) {
        try {
            return HouseDto.builder()
                    .id(housePayment.getHouse().getId())
                    .houseNumber(housePayment.getHouse().getHouseNumber())
                    .streetName(housePayment.getHouse().getStreetName())
                    .user(mapToUserDetailsDto(housePayment))
                    .build();
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void validateIfHousesAreSelected(PaymentOperationDto paymentOperationDto) {
        if (paymentOperationDto.getHouseIds() == null || paymentOperationDto.getHouseIds().size() < 1) {
            throw ResponseException.builder()
                    .message("There is no houses selected for payment operation creation")
                    .build();
        }
    }

    public void validatePaymentOperationId(PaymentOperationDto paymentOperationDto) {
        if (paymentOperationDto.getId() == null) {
            throw ResponseException.builder()
                    .message("Payment operation id is null")
                    .build();
        }
    }

    public void validatesSelectedHousesIsWithOwners(Long selectedHouseId) {
        if (!houseService.checkIfHouseHasOwner(selectedHouseId)) {
            throw ResponseException.builder()
                    .message("Your selected house with ID " + (selectedHouseId) + " does not has an owner and cannot be added to the payment operation")
                    .build();
        }
    }

    public void validatePaymentOperationTotalSum(PaymentOperationDto paymentOperationDto){
        if(paymentOperationDto.getTotalSum() == null || paymentOperationDto.getTotalSum() == 0) {
            throw ResponseException.builder()
                    .message("Your total sum of the payment operation cannot be null or 0")
                    .build();
        }
    }
    public void validatePaymentOperationDueDate(PaymentOperationDto paymentOperationDto){
        if (paymentOperationDto.getDueDate() == null || paymentOperationDto.getDueDate().isBefore(LocalDate.now())){
            throw ResponseException.builder()
                    .message("Given due date is null or less than todays date")
                    .build();
        }
    }


    @Override
    public Boolean checkIfIdExists(Long id) {
        if (paymentOperationRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .systemMessage("Payment operation id is non existent")
                    .build();
        }
    }
}
