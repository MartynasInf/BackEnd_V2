package com.example.Street_manager.service;

import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.enums.Role;
import com.example.Street_manager.model.User;
import com.example.Street_manager.repository.HouseRepository;
import com.example.Street_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements DataChecker<User> {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseService houseService;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw ResponseException.builder()
                    .message("Users was not found")
                    .build();
        }
        return users;
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void createNewUser(UserDetailsDto request) {
        try {
            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .bankAccount(request.getBankAccount())
                    .house(houseRepository.findById(request.getHouse()).get())
                    .enabled(true)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void updateExistingUserDetails(UserDetailsDto request) {
        try {
            User user = User.builder()
                    .id(request.getId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .role(userRepository.getReferenceById(request.getId()).getRole())
                    .house(userRepository.findById(request.getId()).get().getHouse())
                    .enabled(request.getEnabled())
                    .bankAccount(request.getBankAccount())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                user.setPassword(userRepository.findById(request.getId()).get().getPassword());
            }
            userRepository.save(user);
        } catch (Exception e) {
            throw ResponseException.builder()
                    .message(e.getMessage())
                    .build();
        }
    }

    public void validateUserId(UserDetailsDto userDetailsDto) {
        if (userDetailsDto.getId() == null) {
            throw ResponseException.builder()
                    .message("User id is null")
                    .build();
        }
    }

    public void validateGivenUserDetailsFields(UserDetailsDto user, List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field field = UserDetailsDto.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(user);
                if (value == null || value.toString().isEmpty()) {
                    throw ResponseException.builder()
                            .message("Your given value of the requested field '" + fieldName + "' is null or empty")
                            .build();
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw ResponseException.builder()
                        .message(e.getMessage())
                        .build();
            }
        }
    }

    public void validateUserDetailsHasHouseId(UserDetailsDto request) {
        if (request.getHouse() == null) {
            throw ResponseException.builder()
                    .message("House id in your request is null")
                    .build();
        }
    }

    public void validateHouseInUserDetailsIsFreeToUse(UserDetailsDto request) {
        if (houseService.isHouseOwnerPresent(request.getHouse())) {
            throw ResponseException.builder()
                    .message("House already has an owner and cannot be assigned to another user")
                    .build();
        }
    }

    @Override
    public Boolean checkIfIdExists(Long id) {
        if (userRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .message("User id is non existent")
                    .build();
        }
    }
}
