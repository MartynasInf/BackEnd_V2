package com.example.Street_manager.service;

import com.example.Street_manager.dto.HouseDto;
import com.example.Street_manager.dto.UserDetailsDto;
import com.example.Street_manager.Interface.DataChecker;
import com.example.Street_manager.exception.ResponseException;
import com.example.Street_manager.model.House;

import com.example.Street_manager.model.User;
import com.example.Street_manager.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseService implements DataChecker<House> {

    private final HouseRepository houseRepository;

    public List<HouseDto> findAll() {
        List<HouseDto> houses = mapToHousesDto();
        if (houses.isEmpty()) {
            throw ResponseException.builder()
                    .message("Houses was not found")
                    .build();
        }
        return houses;
    }

    private List<HouseDto> mapToHousesDto() {
        return houseRepository.findAll().stream()
                .map(this::mapHouseToDto)
                .collect(Collectors.toList());
    }

    private HouseDto mapHouseToDto(House house) {
        return HouseDto.builder()
                .id(house.getId())
                .houseNumber(house.getHouseNumber())
                .streetName(house.getStreetName())
                .user(mapToUserDetails(house.getUser()))
                .plotArea(house.getPlotArea())
                .build();
    }

    private UserDetailsDto mapToUserDetails(User user) {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        if (user != null) {
            userDetailsDto.setId(user.getId());
            userDetailsDto.setFirstName(user.getFirstName());
            userDetailsDto.setLastName(user.getLastName());
            userDetailsDto.setEmail(user.getEmail());
            userDetailsDto.setPhoneNumber(user.getPhoneNumber());
            userDetailsDto.setBankAccount(user.getBankAccount());
            userDetailsDto.setEnabled(user.getEnabled());
        }
        return userDetailsDto;
    }

    public void updateHouseInfo(HouseDto houseInfo) {
        House house = House.builder()
                .id(houseInfo.getId())
                .streetName(houseInfo.getStreetName())
                .houseNumber(houseInfo.getHouseNumber())
                .plotArea(houseInfo.getPlotArea())
                .build();
        houseRepository.save(house);
    }

    public void createNewHouse(HouseDto houseInfo) {
        House house = House.builder()
                .streetName(houseInfo.getStreetName())
                .houseNumber(houseInfo.getHouseNumber())
                .plotArea(houseInfo.getPlotArea())
                .build();
        houseRepository.save(house);
    }

    public void delete(Long houseId) {
        if (isHouseOwnerPresent(houseId)) {
            throw ResponseException.builder()
                    .message("House with owner cannot be deleted. " +
                            "Please remove owner from house before delete.")
                    .build();
        } else {
            houseRepository.deleteById(houseId);
        }
    }

    public Boolean isHouseOwnerPresent(Long id) {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> ResponseException.builder()
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
