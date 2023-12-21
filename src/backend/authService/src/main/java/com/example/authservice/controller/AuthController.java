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
    @Operation(operationId = "generateJwtToken", tags = {"auth"},
            summary = "Create Jwt encrypted token ", description =
            "Create Jwt encrypted token, by providing Basic authentication (username, password)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Jwt token acquired correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized to access this URL with this HTTP method",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "403", description =
                    "Forbidden to get token because of no roles are available for the authenticated employee",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> generateJwtToken(Authentication authentication) {
        return ResponseEntity.ok(jwtService.generateJwtToken(authentication));
    }

}
