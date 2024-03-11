package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(Path.V1_ROLES)
@RequiredArgsConstructor
public class RoleController {

    /* Service Beans */
    private final RoleService roleService;

    /* Public Methods */

    @PostMapping
    @Operation(operationId = "createRole", tags = {"roles"},
            summary = "Create a new role", description =
            "Create a new role by providing its name and microservices where it operates.<br>" +
            "Information about it are available in URI given in the response header location.<br>" +
            "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Role created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Name : 32 characters" +
                    "Microservices : check alias of available microservices",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createRole(
            @Valid @RequestBody RoleCreationRequestDto roleCreationRequestDto)
    {
        // try to create a new role
        String role = roleService.createRole(roleCreationRequestDto);
        // generate URI location to inform the client how to get information on the new role
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ROLE_NAME)
                .buildAndExpand(role)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(operationId = "getAllRoleNames", tags = {"roles"},
            summary = "Retrieve all role names", description =
            "Retrieve all role names.<br>" +
            "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role names retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = String.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<Set<String>> getAllRoleNames()
    {
        return ResponseEntity.ok(roleService.getAllRoleNames());
    }

    @GetMapping(Path.MICROSERVICES)
    @Operation(operationId = "getAllRolesAndMicroservices", tags = {"roles"},
            summary = "Retrieve all role names and microservices alias", description =
            "Retrieve all roles names and microservices alias.<br>" +
                    "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role names and microservices alias retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RolesMicroservicesResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<RolesMicroservicesResponseDto> getAllRolesAndMicroservices()
    {
        return ResponseEntity.ok(roleService.getAllRolesAndMicroservices());
    }

    @GetMapping(Path.ROLE_NAME)
    @Operation(operationId = "getRoleByName", tags = {"roles"},
            summary = "Retrieve one role permissions and activation status", description =
            "Retrieve one role permissions and activation status, by providing its name.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role permissions and activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RoleResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<RoleResponseDto> getRoleByName(
            @PathVariable String role)
    {
        return ResponseEntity.ok(roleService.getRoleByName(role));
    }

    @GetMapping(Path.ROLE_NAME_ACTIVATION)
    @Operation(operationId = "getRoleActivationByRoleAndMicroservice", tags = {"roles"},
            summary = "Retrieve one role activation status for a microservice", description =
            "Retrieve one role activation status for a microservice, " +
                    "by providing its name and the microservice alias.<br>" +
                    "Only available for admins.",
            parameters = {
            @Parameter(name = "role", description =
                    "The role name"),
            @Parameter(name = "microservice", description =
                    "The microservice alias")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleActivationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role activation status not found for role and microservice provided",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<RoleActivationResponseDto> getRoleActivationByRoleAndMicroservice(
            @PathVariable String role,
            @RequestParam("microservice") String microservice)
    {
        return ResponseEntity.ok(roleService.getRoleActivationByRoleAndMicroservice(role, microservice));
    }

    @PutMapping(Path.ROLE_NAME)
    @Operation(operationId = "updateRoleByName", tags = {"roles"},
            summary = "Update a role permissions and / or activation status", description =
            "Update a role permissions and / or activation status, by providing a list of active ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Role activation status and / or permissions updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Permissions : retrieve a role information for the microservice, " +
                    "to know which one are available.<br>" +
                    "isEnable : provide a boolean to modify value (can be null to keep current value).",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateRoleByName(
            @PathVariable String role,
            @Valid @RequestBody RoleUpdateRequestDto roleDto)
    {
        roleService.updateRoleByName(role, roleDto);
        return ResponseEntity.noContent().build();
    }
}
