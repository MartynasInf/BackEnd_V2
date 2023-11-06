package com.example.Street_manager.service;

import com.example.Street_manager.dto.HousePaymentDto;
import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.HousePayment;
import com.example.Street_manager.repository.HousePaymentRepository;
import com.example.Street_manager.repository.HouseRepository;
import com.example.Street_manager.repository.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HousePaymentService implements DataChecker<HousePayment> {

    private final HousePaymentRepository housePaymentRepository;
    private final HouseRepository houseRepository;
    private final PaymentOperationRepository paymentOperationRepository;

    public void pay(HousePaymentDto housePaymentDto) {
        try {
            HousePayment housePayment = housePaymentRepository.findById(housePaymentDto.getId())
                    .orElseThrow(() -> ResponseException.builder()
                            .message("Your selected house payment does not exist")
                            .build());
            housePayment.setIsPaid(true);
            housePaymentRepository.save(housePayment);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public List<HousePayment> createHousePayments(PaymentOperationDto paymentRequest, Long paymentId) {
        List<HousePayment> housePayments = new ArrayList<>();
        for (Integer houseId : paymentRequest.getHouseIds()) {
            HousePayment newHousePayment = HousePayment.builder()
                    .amount(calculateHousePayment(paymentRequest))
                    .isPaid(false)
                    .paymentRequest(paymentOperationRepository.findById(paymentId).get())
                    .house(houseRepository.findById(houseId.longValue()).get())
                    .build();
            housePayments.add(newHousePayment);
        }
        return housePayments;
    }

    public Double calculateHousePayment(PaymentOperationDto paymentRequest) {
        Double paymentSizeForHouse = paymentRequest.getTotalSum() / paymentRequest.getHouseIds().size();
        return BigDecimal.valueOf(paymentSizeForHouse).setScale(2, RoundingMode.UP).doubleValue();
    }

    @Override
    public Boolean checkIfIdExists(Long id) {
        if (housePaymentRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .message("House payment id " + id + " is non existent")
                    .build();
        }
    }
}
