package com.example.Street_manager.controller;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.service.HouseService;
import com.example.Street_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorised/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private static final List<String> FIELDS_FOR_HOUSE_CREATION = List.of("houseNumber", "streetName");
    private static final List<String> FIELDS_FOR_HOUSE_UPDATE = List.of("id", "houseNumber", "streetName");

    @GetMapping("/")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(houseService.findAll());
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PostMapping("/admin/add")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> create(@RequestBody HouseDto houseDto) {
        try {
            houseService.validateGivenHouseDetailsFields(houseDto, FIELDS_FOR_HOUSE_CREATION);
            houseService.createNewHouse(houseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("House was created successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/update")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> update(@RequestBody HouseDto houseDto) {
        try {
            houseService.validateGivenHouseDetailsFields(houseDto, FIELDS_FOR_HOUSE_UPDATE);
            houseService.updateHouseInfo(houseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("House was updated successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{houseId}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> delete(@PathVariable Long houseId) {
        try {
            houseService.checkIfIdExists(houseId);
            houseService.delete(houseId);
            return ResponseEntity.status(HttpStatus.OK).body("House was deleted successfully");
        } catch (ResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
