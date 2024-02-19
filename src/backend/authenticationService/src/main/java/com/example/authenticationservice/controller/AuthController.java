package com.example.authenticationservice.controller;

import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    "Unauthorized to access this URL because Basis authentication failed",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "403", description =
                    "Forbidden to get token because of no roles are available for the authenticated login-profile",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> generateJwtToken(Authentication authentication) {
        return ResponseEntity.ok(jwtService.generateJwtToken(authentication.getName()));
    }

}
