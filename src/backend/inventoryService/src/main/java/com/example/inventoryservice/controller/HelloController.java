package com.example.inventoryservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping
    @Operation(operationId = "getHello", tags = {"hello"},
            summary = "GET Hello World !", description =
            "GET Hello World ! from Inventory Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Hello World message retrieved correctly",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")})})
    public ResponseEntity<String> getHello() {

        return ResponseEntity.ok("Hello World from InventoryService!");
    }
}