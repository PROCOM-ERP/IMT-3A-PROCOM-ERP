package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.model.Path;
import com.example.inventoryservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping(Path.V1_ADDRESSES)
public class AddressController {
    final private AddressService addressService;

    @GetMapping(Path.ADDRESS_ID)
    @Operation(operationId = "getAddressById", tags = {"address", "inventory"},
            summary = "Returns the address information",
            description = "Returns the address information with the items at this address",
            parameters = {@Parameter(
                    name = "id",
                    description = "id of the address")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Address information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Address not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<AddressDto> getAddressById(@PathVariable int id){
        return ResponseEntity.ok(addressService.getAddressDtoById(id));
    }

    @GetMapping
    @Operation(operationId = "getAllAddress", tags = {"address", "inventory"},
            summary = "Returns all existing addresses.",
            description = "Returns the addresses information without items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Address information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = AddressDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Address not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<AddressDto>> getAllAddress(){
        return ResponseEntity.ok(addressService.getAllAddress());
    }
}
