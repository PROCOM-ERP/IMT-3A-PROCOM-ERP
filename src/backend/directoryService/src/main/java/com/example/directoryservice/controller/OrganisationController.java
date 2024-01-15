package com.example.directoryservice.controller;

import com.example.directoryservice.dto.OrganisationRequestDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.OrganisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Path.V1_ORGANISATIONS)
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    @PostMapping
    @Operation(operationId = "createOrganisation", tags = {"organisations"},
            summary = "Create a new organisation", description =
            "Create a new organisation by providing a name and an address (see body type).<br>" +
                    "Information about it are available in URI given in the response header location.<br>" +
                    "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Organisation created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints." +
                    "Address : retrieve addresses information (addresses section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createOrganisation(@RequestBody OrganisationRequestDto organisationRequestDto) {
        // try to create a new entity
        String name = organisationService.createOrganisation(organisationRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ORGANISATION_ID_OR_NAME)
                .buildAndExpand(name)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

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
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok().body(organisationService.getAllOrganisations());
    }

    @GetMapping(Path.ORGANISATION_ID_OR_NAME)
    @Operation(operationId = "getOrganisation", tags = {"organisations"},
            summary = "Retrieve one organisations information", description =
            "Retrieve one organisations information, by providing its id or name.",
            parameters = {@Parameter(name = "idOrName", description =
                    "The organisation id as an integer in a string or its name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Organisation information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrganisationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Organisation not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<OrganisationResponseDto> getOrganisation(@PathVariable String idOrName) {
        return ResponseEntity.ok().body(organisationService.getOrganisation(idOrName));
    }

    @PatchMapping(Path.ORGANISATION_ID_OR_NAME_ADDRESS)
    @Operation(operationId = "updateOrganisationAddress", tags = {"organisations"},
            summary = "Update an organisation address", description =
            "Update an organisation address, by providing a new address as an integer.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idOrName", description =
                    "The organisation id as an integer in a string or its name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Organisation address updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Organisation not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Address : retrieve addresses information (addresses section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateOrganisationAddress(@PathVariable String idOrName,
                                                                @RequestBody Integer idAddress) {
        organisationService.updateOrganisationAddress(idOrName, idAddress);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.ORGANISATION_ID_OR_NAME)
    @Operation(operationId = "deleteOrganisation", tags = {"organisations"},
            summary = "Delete an organisation", description =
            "Delete an organisation, by providing its id or name.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idOrName", description =
                    "The organisation id as an integer in a string or its name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Organisation deleted correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Organisation not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> deleteOrganisation(@PathVariable String idOrName) {
        organisationService.deleteOrganisation(idOrName);
        return ResponseEntity.noContent().build();
    }

}
