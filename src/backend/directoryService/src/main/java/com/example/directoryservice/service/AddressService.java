package com.example.directoryservice.service;

import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Address;
import com.example.directoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    // private final Logger logger = LoggerFactory.getLogger(AddressService.class);

    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(AddressService::modelToResponseDto)
                .toList();
    }

    private static AddressResponseDto modelToResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .number(address.getNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipcode(address.getZipcode())
                .info(address.getInfo())
                .build();
    }

}
