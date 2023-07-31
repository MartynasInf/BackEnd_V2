package com.example.Street_manager.service;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.House;

import com.example.Street_manager.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService implements DataChecker<House> {

    private final HouseRepository houseRepository;

    public List<HouseDto> findAll() {
        List<HouseDto> houses = mapToHouseDto();
        if (houses.isEmpty()) {
            throw ResponseException.builder()
                    .message("Houses was not found")
                    .build();
        }
        return houses;
    }

    private List<HouseDto> mapToHouseDto() {
        List<HouseDto> housesDto = new ArrayList<>();
        List<House> houses = houseRepository.findAll();
        for (House house : houses) {
            HouseDto houseDto = HouseDto.builder()
                    .id(house.getId())
                    .houseNumber(house.getHouseNumber())
                    .streetName(house.getStreetName())
                    .user(mapToUserDetails(house))
                    .build();
            housesDto.add(houseDto);
        }
        return housesDto;
    }

    private UserDetailsDto mapToUserDetails(House house) {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        if (house.getUser() != null) {
            userDetailsDto.setId(house.getUser().getId());
            userDetailsDto.setFirstName(house.getUser().getFirstName());
            userDetailsDto.setLastName(house.getUser().getLastName());
            userDetailsDto.setEmail(house.getUser().getEmail());
            userDetailsDto.setPhoneNumber(house.getUser().getPhoneNumber());
            userDetailsDto.setBankAccount(house.getUser().getBankAccount());
            userDetailsDto.setEnabled(house.getUser().getEnabled());
        }
        return userDetailsDto;
    }

    public void updateHouseInfo(HouseDto houseInfo) {
        House house = House.builder()
                .id(houseInfo.getId())
                .streetName(houseInfo.getStreetName())
                .houseNumber(houseInfo.getHouseNumber())
                .build();
        houseRepository.save(house);
    }

    public void createNewHouse(HouseDto houseInfo) {
        House house = House.builder()
                .streetName(houseInfo.getStreetName())
                .houseNumber(houseInfo.getHouseNumber())
                .build();
        houseRepository.save(house);
    }

    public void delete(Long houseId) {
        if (!checkIfHouseHasOwner(houseId)) {
            houseRepository.deleteById(houseId);
        } else {
            throw ResponseException.builder()
                    .message("House with owner cannot be deleted. " +
                            "Please remove owner from house before delete.")
                    .build();
        }
    }

    public Boolean checkIfHouseHasOwner(Long id) {
        House house = houseRepository.findById(id).orElseThrow(() -> ResponseException.builder()
                .message("House Id is non existent")
                .build());
        return house.getUser() != null;
    }

    public void validateGivenHouseDetailsFields(HouseDto houseDto, List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field field = HouseDto.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(houseDto);
                if (value == null || value.toString().isEmpty()) {
                    throw ResponseException.builder()
                            .message("Your given value of the requested field '" + fieldName + "' is null or empty")
                            .build();
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw ResponseException.builder()
                        .systemMessage(e.getMessage())
                        .build();
            }
        }
    }

    @Override
    public Boolean checkIfIdExists(Long id) {
        if (houseRepository.existsById(id)) {
            return true;
        } else {
            throw ResponseException.builder()
                    .message("House Id " + id + " is non existent")
                    .build();
        }
    }
}
