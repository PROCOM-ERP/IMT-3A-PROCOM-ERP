package com.example.directoryservice.controller;

import com.example.directoryservice.dto.AddressRequestDto;
import com.example.directoryservice.dto.AddressResponseDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(operationId = "createAddress", tags = {"addresses"},
            summary = "Create a new address", description =
            "Create a new address by providing location information (see body type).<br>" +
            "Information about it are available in URI given in the response header location.<br>" +
                    "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Address created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
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
    @Operation(operationId = "getAllAddresses", tags = {"addresses"},
            summary = "Retrieve all addresses information", description =
            "Retrieve all addresses information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Addresses information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AddressResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        return ResponseEntity.ok().body(addressService.getAllAddresses());
    }

    @GetMapping(Path.ADDRESS_ID)
    @Operation(operationId = "getAddress", tags = {"addresses"},
            summary = "Retrieve one address information", description =
            "Retrieve one address information, by providing its id.",
            parameters = {@Parameter(name = "idAddress", description =
                    "The address id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Address information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Address not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<AddressResponseDto> getAddress(@PathVariable Integer idAddress) {
        return ResponseEntity.ok().body(addressService.getAddress(idAddress));
    }

    @DeleteMapping(Path.ADDRESS_ID)
    @Operation(operationId = "deleteAddress", tags = {"addresses"},
            summary = "Delete an address", description =
            "Delete one address, by providing its id.",
            parameters = {@Parameter(name = "idAddress", description =
                    "The address id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Address deleted correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Address not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> deleteAddress(@PathVariable Integer idAddress) {
        addressService.deleteAddress(idAddress);
        return ResponseEntity.noContent().build();
    }

}
