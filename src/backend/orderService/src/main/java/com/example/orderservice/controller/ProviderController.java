package com.example.orderservice.controller;

import com.example.orderservice.dto.HttpStatusErrorDto;
import com.example.orderservice.model.Path;
import com.example.orderservice.model.Provider;
import com.example.orderservice.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(Path.V1_PROVIDERS)
@RequiredArgsConstructor
public class ProviderController {

    /* Service Beans */

    private final ProviderService providerService;

    /* Endpoints Methods */

    @GetMapping
    @Operation(operationId = "getAllProviders", tags = {"providers"},
            summary = "Retrieve all providers information", description =
            "Retrieve all providers information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Providers information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = Provider.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<Set<Provider>> getAllProviders()
    {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

}
