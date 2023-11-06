package com.example.Street_manager;

import com.example.Street_manager.dto.PaymentOperationDto;
import com.example.Street_manager.repository.HousePaymentRepository;
import com.example.Street_manager.repository.HouseRepository;
import com.example.Street_manager.repository.PaymentOperationRepository;
import com.example.Street_manager.service.HousePaymentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor
class StreetManagerApplicationTests {

	@Mock
	private HousePaymentRepository housePaymentRepository;
	private PaymentOperationRepository paymentRequestRepository;
	private HouseRepository houseRepository;

	@InjectMocks
	private HousePaymentService housePaymentService;

	@Test
	public void shouldCalculateAmountOfPaymentForOneHouse() {
		//Given

		PaymentOperationDto paymentRequestDto = new PaymentOperationDto(1L,
				"Uz vandeni",
				LocalDate.of(2023, 6, 30),
				50.0,
				null,
				null,
				Arrays.asList(1, 2, 3));
		//When
		Double amountToPay = housePaymentService.calculateHousePayment(paymentRequestDto);
		//Then
		assertEquals(16.67, amountToPay);
	}

}
