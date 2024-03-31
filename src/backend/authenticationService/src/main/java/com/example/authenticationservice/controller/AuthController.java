package com.example.authenticationservice.controller;

import com.example.authenticationservice.annotation.LogExecutionTime;
import com.example.authenticationservice.dto.HttpStatusErrorDto;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.JwtService;
import com.example.authenticationservice.utils.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle authentication requests, including generating JWT tokens for users that have successfully
 * authenticated via Basic Authentication. This controller defines the REST API endpoint for JWT token generation,
 * incorporating security checks to ensure that the requester has the appropriate authentication and authorization.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 0.1.0 (2024-01-15)
 */
@RequestMapping(Path.V1_AUTH)
@RestController
@RequiredArgsConstructor
public class AuthController {

    /* Service Beans */

    /**
     * Service {@link JwtService} for JWT token operations, including generation
     * It is used here to generate a new JWT token based on the authenticated user's details.
     *
     * @since 0.1.0
     */
    private final JwtService jwtService;

    /* Endpoints Methods */

    /**
     * Generates a new JWT token for an authenticated user. The token is generated based on the username
     * of the authenticated user, which is extracted from the provided {@link Authentication} object.
     * Requires that the user has successfully authenticated via Basic Authentication before calling this method.
     *
     * @param authentication An {@link Authentication} object containing the user's authentication details.
     * @return A {@link ResponseEntity} containing the generated JWT token if the user is authenticated,
     * or an appropriate HTTP error status in case of authentication or authorization failure.
     * @throws InsufficientAuthenticationException if the authentication details are insufficient for token generation.
     * @throws AccessDeniedException if the authenticated user does not have the necessary roles or permissions for token generation.
     * @since 0.1.0
     */
    @PostMapping( Path.JWT)
    @LogExecutionTime(description = "Generate new Jwt token for a user.",
            tag = CustomLogger.TAG_USERS)
    @Operation(operationId = "generateJwtToken", tags = {"auth"},
            summary = "Create Jwt encrypted token ", description =
            "Create Jwt encrypted token, by providing Basic authentication (username, password)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Jwt token acquired correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized to access this URL because Basis authentication failed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) } ),
            @ApiResponse(responseCode = "403", description =
                    "Forbidden to get token because of no roles are available for the authenticated login-profile",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> generateJwtToken(
            Authentication authentication)
            throws InsufficientAuthenticationException,
            AccessDeniedException
    {
        return ResponseEntity.ok(jwtService.generateJwtToken(authentication.getName()));
    }

}
