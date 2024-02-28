package com.example.orderservice.service;

import com.example.orderservice.dto.AddressCreationRequestDto;
import com.example.orderservice.model.Address;
import com.example.orderservice.repository.AddressRepository;
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
        addressDto.setStreet(addressDto.getStreet().trim().toLowerCase());
        addressDto.setCity(addressDto.getCity().trim().toLowerCase());
        if (addressDto.getState() != null)
            addressDto.setState(addressDto.getState().trim().toLowerCase());
        addressDto.setCountry(addressDto.getCountry().trim().toLowerCase());
        addressDto.setZipcode(addressDto.getZipcode().trim().toLowerCase());
        if (addressDto.getInfo() != null)
            addressDto.setInfo(addressDto.getInfo().trim().toLowerCase());
    }


    private String generateHashedIdAddressFromFields(
            AddressCreationRequestDto addressDto)
            throws Exception
    {
        // join all fields before hashing operation
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(addressDto.getNumber().toString());
        joiner.add(addressDto.getStreet());
        joiner.add(addressDto.getCity());
        if (addressDto.getState() != null)
            joiner.add(addressDto.getState());
        joiner.add(addressDto.getCountry());
        joiner.add(addressDto.getZipcode());
        if (addressDto.getInfo() != null)
            joiner.add(addressDto.getInfo());

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
