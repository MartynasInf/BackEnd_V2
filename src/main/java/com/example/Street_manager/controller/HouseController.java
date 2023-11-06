package com.example.Street_manager.controller;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorised/houses")
@RequiredArgsConstructor
public class HouseController {

    private final Logger logger = LogManager.getLogger(HouseController.class);
    private final HouseService houseService;
    private static final List<String> FIELDS_FOR_HOUSE_CREATION = List.of("houseNumber", "streetName");
    private static final List<String> FIELDS_FOR_HOUSE_UPDATE = List.of("id", "houseNumber", "streetName");

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> findAll() {
        try {
            List<HouseDto> houses = houseService.findAll();
            logger.info("Houses has been retrieved successfully");
            return ResponseEntity.status(HttpStatus.OK).body(houses);
        } catch (ResponseException e) {
            logger.error("An error occurred while retrieving houses. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/add")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> create(@RequestBody HouseDto houseDto) {
        try {
            logger.info("Creating a new house " + houseDto.getStreetName() + ". " + houseDto.getHouseNumber());
            houseService.validateGivenHouseDetailsFields(houseDto, FIELDS_FOR_HOUSE_CREATION);
            houseService.createNewHouse(houseDto);
            logger.info("House has been created successfully. Street and number = " + houseDto.getStreetName() + " " + houseDto.getHouseNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body("House was created successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while creating a house " + houseDto.getStreetName() + " " + houseDto.getHouseNumber());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating a house " + houseDto.getStreetName() + " " + houseDto.getHouseNumber());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/update")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> update(@RequestBody HouseDto houseDto) {
        try {
            logger.info("Updating house info for house ID = " + houseDto.getId());
            houseService.validateGivenHouseDetailsFields(houseDto, FIELDS_FOR_HOUSE_UPDATE);
            houseService.updateHouseInfo(houseDto);
            logger.info("House info for house ID = " + houseDto.getId() + "was updated successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body("House was updated successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while updating house info for house ID = " + houseDto.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while updating house info for house ID = " + houseDto.getId() + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{houseId}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> delete(@PathVariable Long houseId) {
        try {
            logger.info("Deleting house with ID = " + houseId);
            houseService.checkIfIdExists(houseId);
            houseService.delete(houseId);
            logger.info("House with ID = " + houseId  + " was deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body("House was deleted successfully");
        } catch (ResponseException e) {
            logger.error("An error occurred while deleting house with ID = " + houseId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while deleting house with ID = " + houseId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
