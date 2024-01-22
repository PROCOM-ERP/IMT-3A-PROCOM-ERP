package com.example.directoryservice.service;

import com.example.directoryservice.dto.AddressRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Address;
import com.example.directoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    // private final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Transactional
    public Integer createAddress(AddressRequestDto addressRequestDto) {
        // create new entity
        Address address = Address.builder()
                .number(addressRequestDto.getNumber())
                .street(addressRequestDto.getStreet())
                .city(addressRequestDto.getCity())
                .state(addressRequestDto.getState())
                .country(addressRequestDto.getCountry())
                .postalCode(addressRequestDto.getPostalCode())
                .info(addressRequestDto.getInfo())
                .build();

        // try to save entity and return its id
        return addressRepository.save(address).getId();
    }

    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(AddressService::modelToResponseDto)
                .toList();
    }

    public AddressResponseDto getAddress(Integer idAddress) throws NoSuchElementException {
        return addressRepository.findById(idAddress)
                .map(AddressService::modelToResponseDto)
                .orElseThrow();
    }

    public void deleteAddress(Integer idAddress) throws NoSuchElementException {
        if (! addressRepository.existsById(idAddress))
            throw new NoSuchElementException();
        addressRepository.deleteById(idAddress);
    }

    private static AddressResponseDto modelToResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .number(address.getNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .info(address.getInfo())
                .organisation(address.getOrganisation() != null ? address.getOrganisation().getName() :  "")
                .services(address.getServices())
                .build();
    }

}
