package com.example.directoryservice.controller;

import com.example.directoryservice.dto.AddressRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.model.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Path.V1_ADDRESSES)
public class AddressController {

    @PostMapping
    public ResponseEntity<String> createAddress(@RequestBody AddressRequestDto addressRequestDto) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(Path.ADDRESS_ID)
    public ResponseEntity<AddressResponseDto> getAddress(@PathVariable String idAddress) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Path.ADDRESS_ID)
    public ResponseEntity<String> deleteAddress(@PathVariable String idAddress) {
        // TODO
        return ResponseEntity.ok().build();
    }

}
