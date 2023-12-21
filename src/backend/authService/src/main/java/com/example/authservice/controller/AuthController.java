package com.example.authservice.controller;

import com.example.authservice.model.Path;
import com.example.authservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RequestMapping(Path.V1_AUTH)
@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping( Path.JWT)
    @Operation(operationId = "getJwtToken", tags = {"auth"},
            summary = "GET Jwt encrypted token ", description =
            "GET Jwt encrypted token from AuthService")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jwt token acquired correctly", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class) )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to get token", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class) )}),
            @ApiResponse(responseCode = "403", description = "Forbidden to get token because of no roles", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class) )})})
    public ResponseEntity<String> getJwtToken(Authentication authentication) {
        try {
            String jwtToken = jwtService.generateJwtToken(authentication);
            return ResponseEntity.ok(jwtToken);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
