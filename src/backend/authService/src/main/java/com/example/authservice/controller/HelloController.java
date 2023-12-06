package com.example.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping
    @Operation(operationId = "getHello", tags = {"hello"}, summary = "GET Hello World !", description =
            "GET Hello World ! from AuthService")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hello World message got correctly", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class) )})})
    public ResponseEntity<String> getHello() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Hello, World !");
    }

}
