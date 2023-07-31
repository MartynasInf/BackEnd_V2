package com.example.Street_manager.config;

import com.example.Street_manager.enums.PaymentOperationStatus;
import com.example.Street_manager.model.House;
import com.example.Street_manager.enums.Role;
import com.example.Street_manager.model.HousePayment;
import com.example.Street_manager.model.PaymentOperation;
import com.example.Street_manager.model.User;
import com.example.Street_manager.repository.PaymentOperationRepository;
import com.example.Street_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final PaymentOperationRepository paymentRequestRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {

            House house1 = House.builder()
                    .houseNumber("6")
                    .streetName("Jurgio Ožeraičio")
                    .build();
            House house2 = House.builder()
                    .streetName("Jurgio Ožeraičio")
                    .houseNumber("4")
                    .build();

            User martynas = User.builder()
                    .email("martynas.jokubauskis@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("martynas"))
                    .lastName("Jokubauskis")
                    .firstName("Martynas")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .house(house1)
                    .build();


            User testas = User.builder()
                    .email("testas@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("testas"))
                    .lastName("Testas")
                    .firstName("Testauskas")
                    .enabled(true)
                    .role(Role.USER)
                    .house(house2)
                    .build();

            PaymentOperation paymentRequest1 = PaymentOperation.builder()
                    .purpose("For water supply 2023-06")
                    .dueDate(LocalDate.of(2023, 6, 30))
                    .totalSum(47.80)
                    .operationStatus(PaymentOperationStatus.CREATION)
                    .build();
            PaymentOperation paymentRequest2 = PaymentOperation.builder()
                    .purpose("For road works 2023-06")
                    .dueDate(LocalDate.of(2023, 6, 30))
                    .totalSum(80.0)
                    .operationStatus(PaymentOperationStatus.CREATION)
                    .build();

            HousePayment housePayment1 = HousePayment.builder()
                    .amount(23.9)
                    .isPaid(false)
                    .house(house1)
                    .paymentRequest(paymentRequest1)
                    .build();
            HousePayment housePayment2 = HousePayment.builder()
                    .amount(23.9)
                    .isPaid(false)
                    .house(house2)
                    .paymentRequest(paymentRequest1)
                    .build();
            HousePayment housePayment3 = HousePayment.builder()
                    .amount(80.0)
                    .isPaid(false)
                    .house(house2)
                    .paymentRequest(paymentRequest2)
                    .build();

            paymentRequest1.setHousePayments(Arrays.asList(housePayment1, housePayment2));
            paymentRequest2.setHousePayments(Collections.singletonList(housePayment3));
            userRepository.save(martynas);
            userRepository.save(testas);
            paymentRequestRepository.save(paymentRequest1);
            paymentRequestRepository.save(paymentRequest2);
        };
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        //DAO - DATA ACCES OBJECT
        DaoAuthenticationProvider authProvider  = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
