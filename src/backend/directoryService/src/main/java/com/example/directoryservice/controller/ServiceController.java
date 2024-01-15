package com.example.directoryservice.controller;

import com.example.directoryservice.dto.ServiceRequestDto;
import com.example.directoryservice.dto.ServiceResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.ServiceService;
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
@RequestMapping(Path.V1_SERVICES)
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @Operation(operationId = "createService", tags = {"services"},
            summary = "Create a new service", description =
            "Create a new service by providing a name, an address and an organisation(see body type).<br>" +
                    "Information about it are available in URI given in the response header location.<br>" +
                    "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Service created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints." +
                    "Address : retrieve addresses information (addresses section) to know which one are available.<br>" +
                    "Organisation : retrieve organisations information (organisations section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createService(@RequestBody ServiceRequestDto serviceRequestDto) {
        // try to create a new entity
        Integer idService = serviceService.createService(serviceRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.SERVICE_ID)
                .buildAndExpand(idService)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(operationId = "getAllServices", tags = {"services"},
            summary = "Retrieve all services information", description =
            "Retrieve all services information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Services information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ServiceResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return ResponseEntity.ok().body(serviceService.getAllServices());
    }

    @GetMapping(Path.SERVICE_ID)
    @Operation(operationId = "getService", tags = {"services"},
            summary = "Retrieve one service information", description =
            "Retrieve one service information, by providing its id.",
            parameters = {@Parameter(name = "idService", description =
                    "The service id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Service information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Service not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable Integer idService) {
        return ResponseEntity.ok().body(serviceService.getService(idService));
    }

    @PatchMapping(Path.SERVICE_ID_ADDRESS)
    @Operation(operationId = "updateServiceAddress", tags = {"services"},
            summary = "Update a service address", description =
            "Update a service address, by providing a new address as an integer.<br>" +
                    "Only available for admins.",
            parameters = {@Parameter(name = "idOrName", description =
                    "The service id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Service address updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Service not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Address : retrieve addresses information (addresses section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateServiceAddress(@PathVariable Integer idService,
                                                       @RequestBody Integer idAddress) {
        serviceService.updateServiceAddress(idService, idAddress);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.SERVICE_ID_ORGANISATION)
    @Operation(operationId = "updateServiceOrganisation", tags = {"services"},
            summary = "Update a service organisation", description =
            "Update a service organisation, by providing a new organisation id.<br>" +
                    "Only available for admins.",
            parameters = {@Parameter(name = "idOrName", description =
                    "The service id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Service organisation updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Service not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Organisation : retrieve organisations information (organisations section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateServiceOrganisation(@PathVariable Integer idService,
                                                            @RequestBody Integer idOrganisation) {
        serviceService.updateServiceOrganisation(idService, idOrganisation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.SERVICE_ID)
    @Operation(operationId = "deleteService", tags = {"services"},
            summary = "Delete a service", description =
            "Delete a service, by providing its id.<br>" +
                    "Only available for admins.",
            parameters = {@Parameter(name = "idService", description =
                    "The service id as an integer")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Service deleted correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Service not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> deleteService(@PathVariable Integer idService) {
        serviceService.deleteServiceById(idService);
        return ResponseEntity.noContent().build();
    }

}
