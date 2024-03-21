package com.example.directoryservice.service;

import com.example.directoryservice.annotation.LogExecutionTime;
import com.example.directoryservice.dto.AddressCreationRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Address;
import com.example.directoryservice.repository.AddressRepository;
import com.example.directoryservice.utils.CustomLogger;
import com.example.directoryservice.utils.CustomStringUtils;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    /* Repository Beans */

    private final AddressRepository addressRepository;

    /* Utils Beans */
    private final CustomStringUtils customStringUtils;

    /* Public Methods */

    @Transactional
    @LogExecutionTime(description = "Create new address.",
            tag = CustomLogger.TAG_ADDRESSES)
    public void createAddress(AddressCreationRequestDto addressDto)
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

        // create new Address entity and save it
        addressRepository.save(creationRequestDtoToModel(idAddress, addressDto));
    }

    @LogExecutionTime(description = "Retrieve all addresses.",
            tag = CustomLogger.TAG_ADDRESSES)
    public Set<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .collect(Collectors.toSet());
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

        addressDto.setStreet(customStringUtils.sanitizeString(addressDto.getStreet()));
        addressDto.setCity(customStringUtils.sanitizeString(addressDto.getCity()));
        if (addressDto.getState() != null)
            addressDto.setState(customStringUtils.sanitizeString(addressDto.getState()));
        addressDto.setCountry(customStringUtils.sanitizeString(addressDto.getCountry()));
        addressDto.setZipcode(customStringUtils.sanitizeString(addressDto.getZipcode()));
        if (addressDto.getInfo() != null)
            addressDto.setInfo(customStringUtils.sanitizeString(addressDto.getInfo()));
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

    AddressResponseDto modelToResponseDto(Address address) {
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
