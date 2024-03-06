package com.example.orderservice.service;

import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.dto.AddressCreationRequestDto;
import com.example.orderservice.model.Address;
import com.example.orderservice.repository.AddressRepository;
import com.example.orderservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    /* Public Methods */
    @LogExecutionTime(description = "Create new address.",
            tag = CustomLogger.TAG_ADDRESSES)
    public Address createAddress(AddressCreationRequestDto addressDto)
            throws Exception
    {
        // check fields before sanitization
        if (addressDto.getState() != null && addressDto.getState().isBlank())
            throw new IllegalArgumentException("Address state field cannot be blank if provided");
        if (addressDto.getInfo() != null && addressDto.getInfo().isBlank())
            throw new IllegalArgumentException("Address info field cannot be blank if provided");

        // sanitize fields before Address entity creation
        sanitizeAddressCreationRequestDto(addressDto);

        // create Address entity hashed id
        String idAddress = generateHashedIdAddressFromFields(addressDto);

        // retrieve Address entity if it already exists, else create and save it
        return addressRepository
                .findById(idAddress)
                .orElse(addressRepository.save(
                        creationRequestDtoToModel(idAddress, addressDto)));
    }

    /* Private Methods */
    private Address creationRequestDtoToModel(String idAddress, AddressCreationRequestDto addressDto)
    {
        // create Address entity
        return Address.builder()
                .id(idAddress)
                .number(addressDto.getNumber())
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .country(addressDto.getCountry())
                .zipcode(addressDto.getZipcode())
                .info(addressDto.getInfo())
                .build();
    }

    private void sanitizeAddressCreationRequestDto(AddressCreationRequestDto addressDto)
    {
        // sanitize fields before Address entity creation
        addressDto.setStreet(addressDto.getStreet().trim());
        addressDto.setCity(addressDto.getCity().trim());
        if (addressDto.getState() != null)
            addressDto.setState(addressDto.getState().trim());
        addressDto.setCountry(addressDto.getCountry().trim());
        addressDto.setZipcode(addressDto.getZipcode().trim());
        if (addressDto.getInfo() != null)
            addressDto.setInfo(addressDto.getInfo().trim());
    }


    private String generateHashedIdAddressFromFields(
            AddressCreationRequestDto addressDto)
            throws Exception
    {
        // join all fields (except state cause included in zipcode field) before hashing operation
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(addressDto.getNumber().toString());
        joiner.add(addressDto.getStreet().toLowerCase());
        joiner.add(addressDto.getCity().toLowerCase());
        joiner.add(addressDto.getCountry().toLowerCase());
        joiner.add(addressDto.getZipcode().toLowerCase());
        if (addressDto.getInfo() != null)
            joiner.add(addressDto.getInfo().toLowerCase());

        // hash char sequence and convert it into hexadecimal format
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedId = digest.digest(joiner.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedId);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e);
        }
    }
}
