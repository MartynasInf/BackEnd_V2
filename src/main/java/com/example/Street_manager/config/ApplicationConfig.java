package com.example.Street_manager.config;

import com.example.Street_manager.enums.OperationStatus;
import com.example.Street_manager.model.*;
import com.example.Street_manager.enums.Role;
import com.example.Street_manager.repository.PaymentOperationRepository;
import com.example.Street_manager.repository.UserRepository;
import com.example.Street_manager.repository.VoteAnswerRepository;
import com.example.Street_manager.repository.VotingOperationRepository;
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
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final PaymentOperationRepository paymentRequestRepository;
    private final VotingOperationRepository votingRequestRepository;
    private final VoteAnswerRepository voteAnswerRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            House house1 = House.builder()
                    .houseNumber("6")
                    .streetName("Jurgio Ožeraičio")
                    .plotArea(8.0)
                    .build();
            House house2 = House.builder()
                    .streetName("Jurgio Ožeraičio")
                    .houseNumber("4")
                    .plotArea(9.1)
                    .build();
            User martynas = User.builder()
                    .email("martynas.jokubauskis@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("martynas"))
                    .lastName("Jokubauskis")
                    .firstName("Martynas")
                    .phoneNumber("+370 88666999")
                    .bankAccount("LT123456789")
                    .role(Role.SUPERADMIN)
                    .enabled(true)
                    .house(house1)
                    .build();
            User testas = User.builder()
                    .email("testas@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("testas"))
                    .lastName("Testas")
                    .firstName("Testauskas")
                    .phoneNumber("+370 6655443322")
                    .bankAccount("LT1223344556")
                    .enabled(true)
                    .role(Role.USER)
                    .house(house2)
                    .build();
            PaymentOperation paymentRequest1 = PaymentOperation.builder()
                    .purpose("For water supply 2023-06")
                    .dueDate(LocalDate.of(2024, 12, 30))
                    .totalSum(47.80)
                    .operationStatus(OperationStatus.CREATED)
                    .creator("Martynas Jokubauskis")
                    .creationDate(LocalDate.of(2023, 10, 10))
                    .build();
            PaymentOperation paymentRequest2 = PaymentOperation.builder()
                    .purpose("For road works 2023-06")
                    .dueDate(LocalDate.of(2024, 11, 30))
                    .totalSum(80.0)
                    .operationStatus(OperationStatus.CREATED)
                    .creator("Testas Testauskas")
                    .creationDate(LocalDate.of(2023, 10, 16))
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

            VoteAnswer voteAnswer1 = VoteAnswer.builder()
                    .answer("Pilame skalda")
                    .build();
            VoteAnswer voteAnswer2 = VoteAnswer.builder()
                    .answer("Klojame trinkeles")
                    .build();

            VotingOperation votingRequest = VotingOperation.builder()
                    .title("Ką darome su mūsų keliu")
                    .description("Nusprendžiame kaip darome, po nubalsavimo bus imamasi veiksmų")
                    .finishDate(LocalDate.of(2024, 11, 30))
                    .progress(1)
                    .operationStatus(OperationStatus.CREATED)
                    .votingUsers(Arrays.asList(martynas, testas))
                    .voteAnswers(Arrays.asList(voteAnswer1, voteAnswer2))
                    .creator(martynas.getFirstName() + " " + martynas.getLastName())
                    .build();
            voteAnswer1.setVotingRequest(votingRequest);
            voteAnswer2.setVotingRequest(votingRequest);
            votingRequestRepository.save(votingRequest);
            VoteAnswer voteAnswerSaved = voteAnswerRepository.findById(1L).orElseThrow();
            voteAnswerSaved.setVotedUsers(List.of(testas));
            voteAnswerRepository.save(voteAnswerSaved);
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
