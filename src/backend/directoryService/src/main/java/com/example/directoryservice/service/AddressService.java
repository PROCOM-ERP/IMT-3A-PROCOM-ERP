package com.example.directoryservice.service;

import com.example.directoryservice.dto.AddressCreationRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Address;
import com.example.directoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    // private final Logger logger = LoggerFactory.getLogger(AddressService.class);

    /* Public Methods */

    @Transactional
    public String createAddress(AddressCreationRequestDto addressDto) throws Exception {
        // create new entity
        Address address = Address.builder()
                .id(generateHashedIdAddressFromFields(addressDto))
                .number(addressDto.getNumber())
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .country(addressDto.getCountry())
                .zipcode(addressDto.getZipcode())
                .info(addressDto.getInfo())
                .build();

        // try to save entity and return its id
        return addressRepository.save(address).getId();
    }

    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .toList();
    }

    /* Private Methods */

    private String generateHashedIdAddressFromFields(AddressCreationRequestDto addressDto)
            throws Exception {
        // join all fields before hashing operation
        StringJoiner joiner = new StringJoiner("|");
        if (addressDto.getNumber() != null) joiner.add(addressDto.getNumber().toString());
        if (addressDto.getStreet() != null) joiner.add(addressDto.getStreet());
        if (addressDto.getCity() != null) joiner.add(addressDto.getCity());
        if (addressDto.getState() != null) joiner.add(addressDto.getState());
        if (addressDto.getCountry() != null) joiner.add(addressDto.getCountry());
        if (addressDto.getZipcode() != null) joiner.add(addressDto.getZipcode());
        if (addressDto.getInfo() != null) joiner.add(addressDto.getInfo());

        // hash char sequence and convert it into hexadecimal format
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedId = digest.digest(joiner.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedId);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e);
        }
    }

    private AddressResponseDto modelToResponseDto(Address address) {
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
