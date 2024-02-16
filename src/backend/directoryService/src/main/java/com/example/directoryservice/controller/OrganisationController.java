package com.example.directoryservice.controller;

import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.OrganisationService;
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
@RequestMapping(Path.V1_ORGANISATIONS)
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    @GetMapping
    @Operation(operationId = "getAllOrganisations", tags = {"organisations"},
            summary = "Retrieve all organisations information", description =
            "Retrieve all organisations information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Organisations information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = OrganisationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<Set<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok().body(organisationService.getAllOrganisations());
    }
}
