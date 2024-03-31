package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.HttpStatusErrorDto;
import com.example.authenticationservice.model.Path;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles HTTP GET requests to return a simple "Hello World" message.
 * This class serves as a basic demonstration of a RESTful service endpoint within the AuthenticationService.
 * It responds with a static message, primarily used to test the availability and responsiveness of the service.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 0.1.0 (2024-01-15)
 */
@RestController
@RequestMapping(Path.V1_HELLO)
public class HelloController {

    /* Endpoints Methods */

    /**
     * Handles HTTP GET requests by returning a "Hello World" message from the AuthenticationService.
     * This endpoint serves as a basic example and initial test point to verify that the service is operational.
     *
     * @return A {@link ResponseEntity} containing the "Hello World" message.
     * @since 0.1.0
     */
    @GetMapping(produces = "application/json")
    @Operation(operationId = "getHello", tags = {"hello"},
            summary = "GET Hello World !", description =
            "GET Hello World ! from AuthService")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Hello message retrieved correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) } )})
    public ResponseEntity<String> getHello()
    {
        return ResponseEntity.ok("Hello World from AuthenticationService!");
    }

}

