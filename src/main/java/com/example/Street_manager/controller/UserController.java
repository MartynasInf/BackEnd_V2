package com.example.Street_manager.controller;

import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.User;
import com.example.Street_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorised/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = LogManager.getLogger(UserController.class);
    private static final List<String> FIELDS_FOR_ACCOUNT_CREATION = List.of("firstName", "lastName", "email", "password", "bankAccount", "phoneNumber");
    private static final List<String> FIELDS_FOR_ACCOUNT_EDIT = List.of("firstName", "lastName", "email", "bankAccount", "phoneNumber");

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            logger.info("All users has been retrieved");
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (ResponseException e) {
            logger.error("An error occurred while retrieving users. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while retrieving users. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/register")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> addNew(@RequestBody UserDetailsDto registerRequest) {
        try {
            logger.info("Registering new user");
            userService.validateUserDetailsHasHouseId(registerRequest);
            userService.validateHouseInUserDetailsIsFreeToUse(registerRequest);
            userService.validateGivenUserDetailsFields(registerRequest, FIELDS_FOR_ACCOUNT_CREATION);
            userService.createNewUser((registerRequest));
            logger.info("New user was registered successfully: Email = " + registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("New user was created successfully");
        } catch (ResponseException e) {
            logger.error("Error occurred while registering a new user. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while registering new user. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{userId}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        try {
            logger.info("Deleting user: ID = " + userId);
            userService.checkIfIdExists(userId);
            userService.delete(userId);
            logger.info("User deleted successfully: ID = " + userId);
            return ResponseEntity.status(HttpStatus.OK).body("User was deleted successfully");
        } catch (ResponseException e) {
            logger.error("Error occurred while deleting a user with ID = " + userId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while deleting a user with ID = " + userId + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/")
    @CrossOrigin(origins = "http://localhost:4200/")
    public ResponseEntity<?> update(@RequestBody UserDetailsDto userDetails) {
        try {
            logger.info("Updating user details: ID = " + userDetails.getId());
            userService.validateUserId(userDetails);
            userService.checkIfIdExists(userDetails.getId());
            userService.validateGivenUserDetailsFields(userDetails, FIELDS_FOR_ACCOUNT_EDIT);
            userService.updateExistingUserDetails(userDetails);
            logger.info("User details updated successfully: ID = " + userDetails.getId());
            return ResponseEntity.status(HttpStatus.OK).body("User info has been updated successfully");
        } catch (ResponseException e) {
            logger.error("Error occurred while updating user details to user ID = " + userDetails.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
