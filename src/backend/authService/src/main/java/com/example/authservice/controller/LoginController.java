package com.example.authservice.controller;

import com.example.authservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/login")
@RestController
public class LoginController {

    private final JwtService jwtService;

    public LoginController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/token", produces = "application/json")
    @Operation(
            summary = "GET JWT encrypted token ",
            description = "GET JWT encrypted token from AuthService",
            operationId = "getToken",
            tags = {"login"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jwt token gained correctly", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class) )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to get token", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class))})
    })
    public ResponseEntity<String> getToken(Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwtService.generateToken(authentication));
    }

}
