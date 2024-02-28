package com.example.orderservice.service;

import com.example.orderservice.dto.AddressCreationRequestDto;
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

    /* Public Methods */
    private String generateHashedIdAddressFromFields(AddressCreationRequestDto addressDto)
            throws Exception {
        // join all fields before hashing operation
        StringJoiner joiner = new StringJoiner("|");
        if (addressDto.getNumber() != null)
            joiner.add(addressDto.getNumber().toString());
        if (addressDto.getStreet() != null && ! addressDto.getStreet().isBlank())
            joiner.add(addressDto.getStreet().trim().toLowerCase());
        if (addressDto.getCity() != null && ! addressDto.getCity().isBlank())
            joiner.add(addressDto.getCity().trim().toLowerCase());
        if (addressDto.getState() != null && ! addressDto.getState().isBlank())
            joiner.add(addressDto.getState().trim().toLowerCase());
        if (addressDto.getCountry() != null && ! addressDto.getCountry().isBlank())
            joiner.add(addressDto.getCountry().trim().toLowerCase());
        if (addressDto.getZipcode() != null && ! addressDto.getZipcode().isBlank())
            joiner.add(addressDto.getZipcode().trim().toLowerCase());
        if (addressDto.getInfo() != null && ! addressDto.getInfo().isBlank())
            joiner.add(addressDto.getInfo().trim().toLowerCase());

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
