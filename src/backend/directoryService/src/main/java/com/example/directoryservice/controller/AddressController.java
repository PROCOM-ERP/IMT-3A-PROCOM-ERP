package com.example.directoryservice.controller;

import com.example.directoryservice.dto.AddressRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Path.V1_ADDRESSES)
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<String> createAddress(@RequestBody AddressRequestDto addressRequestDto) {
        // try to create a new entity
        Integer idAddress = addressService.createAddress(addressRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ADDRESS_ID)
                .buildAndExpand(idAddress)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        return ResponseEntity.ok().body(addressService.getAllAddresses());
    }

    @GetMapping(Path.ADDRESS_ID)
    public ResponseEntity<AddressResponseDto> getAddress(@PathVariable Integer idAddress) {
        return ResponseEntity.ok().body(addressService.getAddress(idAddress));
    }

    @DeleteMapping(Path.ADDRESS_ID)
    public ResponseEntity<String> deleteAddress(@PathVariable Integer idAddress) {
        addressService.deleteAddress(idAddress);
        return ResponseEntity.noContent().build();
    }

}
