package com.example.Street_manager.service;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.dto.HousePaymentDto;
import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.enums.PaymentOperationStatus;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.House;
import com.example.Street_manager.model.HousePayment;
import com.example.Street_manager.model.PaymentOperation;
import com.example.Street_manager.model.User;
import com.example.Street_manager.repository.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public PaymentOperation createNewPaymentOperation(PaymentOperationDto paymentOperationDto) {
        try {
            PaymentOperation newPayment = PaymentOperation.builder()
                    .purpose(paymentOperationDto.getPurpose())
                    .dueDate(paymentOperationDto.getDueDate())
                    .totalSum(paymentOperationDto.getTotalSum())
                    .operationStatus(PaymentOperationStatus.CREATED)
                    .creator(paymentOperationDto.getCreator())
                    .creationDate(LocalDate.now())
                    .build();
            newPayment = paymentOperationRepository.save(newPayment);
            newPayment.setHousePayments(housePaymentService.createHousePayments(paymentOperationDto, newPayment.getId()));
            return paymentOperationRepository.save(newPayment);
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

    public void changePaymentOperationDetails(PaymentOperationDto paymentOperationDto){
        PaymentOperation updatedPaymentOperation = PaymentOperation.builder()
                .id(paymentOperationDto.getId())
                .purpose(paymentOperationDto.getPurpose())
                .dueDate(paymentOperationDto.getDueDate())
                .totalSum(paymentOperationDto.getTotalSum())
                .build();
        updatedPaymentOperation.setHousePayments(housePaymentService.createHousePayments(paymentOperationDto, paymentOperationDto.getId()));
    }

    private List<PaymentOperationDto> mapToPaymentOperationDto(List<PaymentOperation> payments) {
        return payments.stream()
                .map(this::mapToPaymentOperationDto)
                .collect(Collectors.toList());
    }

    private PaymentOperationDto mapToPaymentOperationDto(PaymentOperation payment) {
        try {
            return PaymentOperationDto.builder()
                    .id(payment.getId())
                    .purpose(payment.getPurpose())
                    .dueDate(payment.getDueDate())
                    .totalSum(payment.getTotalSum())
                    .operationStatus(payment.getOperationStatus())
                    .housePayments(mapToHousePaymentDto(payment))
                    .creator(payment.getCreator())
                    .creationDate(payment.getCreationDate())
                    .build();
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private List<HousePaymentDto> mapToHousePaymentDto(PaymentOperation payment) {
        return payment.getHousePayments().stream()
                .map(this::mapToHousePaymentDto)
                .collect(Collectors.toList());
    }

    private HousePaymentDto mapToHousePaymentDto(HousePayment housePayment) {
        try {
            return HousePaymentDto.builder()
                    .id(housePayment.getId())
                    .amount(housePayment.getAmount())
                    .isPaid(housePayment.getIsPaid())
                    .house(mapToHouseDto(housePayment.getHouse()))
                    .build();
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private HouseDto mapToHouseDto(House house) {
        try {
            return HouseDto.builder()
                    .id(house.getId())
                    .houseNumber(house.getHouseNumber())
                    .streetName(house.getStreetName())
                    .user(mapToUserDetailsDto(house, house.getUser()))
                    .build();
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    private UserDetailsDto mapToUserDetailsDto(House house, User user) {
        try {
            return UserDetailsDto.builder()
                    .id(house.getUser().getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .house(house.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .bankAccount(user.getBankAccount())
                    .enabled(user.isEnabled())
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

    public void validatePaymentOperationId(Long paymentOperationDtoId) {
        if (paymentOperationDtoId == null) {
            throw ResponseException.builder()
                    .message("Payment operation id is null")
                    .build();
        }
    }

    public void validatesSelectedHousesIsWithOwners(Long selectedHouseId) {
        if (!houseService.isHouseOwnerPresent(selectedHouseId)) {
            throw ResponseException.builder()
                    .message("Your selected house with ID " + (selectedHouseId) + " does not has an owner and cannot be added to the payment operation")
                    .build();
        }
    }

    public void validatePaymentOperationTotalSum(PaymentOperationDto paymentOperationDto) {
        if (paymentOperationDto.getTotalSum() == null || paymentOperationDto.getTotalSum() == 0) {
            throw ResponseException.builder()
                    .message("Your total sum of the payment operation cannot be null or 0")
                    .build();
        }
    }

    public void validatePaymentOperationDueDate(PaymentOperationDto paymentOperationDto) {
        if (paymentOperationDto.getDueDate() == null || paymentOperationDto.getDueDate().isBefore(LocalDate.now())) {
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
