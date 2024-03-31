package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

/**
 * Controller responsible for managing user roles within the AuthenticationService.
 * It handles operations such as creating new roles, retrieving role details,
 * and updating role information. This controller is crucial for maintaining
 * the security aspects of user roles and their permissions across different microservices.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.0.0 (2024-02-22)
 */
@RestController
@RequestMapping(Path.V1_ROLES)
@RequiredArgsConstructor
public class RoleController {

    /* Service Beans */

    /**
     * Service bean for managing roles. Provides functionalities such as creating, retrieving,
     * and updating roles along with their permissions and activation statuses.
     *
     * @since 0.1.0
     */
    private final RoleService roleService;

    /* Endpoints Methods */

    /**
     * Creates a new role with specified permissions and microservice associations.
     * This endpoint is protected and requires admin privileges for access.
     *
     * @param roleCreationRequestDto Request DTO containing details for creating a new role.
     * @return {@link ResponseEntity} with the URI of the newly created role.
     * @since 0.1.0
     */
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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Name : 32 characters" +
                    "Microservices : check alias of available microservices",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
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

    /**
     * Retrieves the names of all roles available in the system.
     * Access to this endpoint is restricted to admin users.
     *
     * @return {@link ResponseEntity} containing a set of role names.
     * @since 0.1.0
     */
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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<Set<String>> getAllRoleNames()
    {
        return ResponseEntity.ok(roleService.getAllRoleNames());
    }

    /**
     * Retrieves detailed information about all roles and the microservices they are associated with.
     * This endpoint is intended for admin use to get an overview of roles and their operational scope.
     *
     * @return {@link ResponseEntity} with details of all roles and their associated microservices.
     * @since 1.0.0
     */
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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<RolesMicroservicesResponseDto> getAllRolesAndMicroservices()
    {
        return ResponseEntity.ok(roleService.getAllRolesAndMicroservices());
    }

    /**
     * Retrieves permissions and activation status of a specific role by its name.
     * This endpoint provides detailed role information, including permissions within a microservice and its activation status.
     * Access is limited to admin users.
     *
     * @param role The name of the role to retrieve.
     * @return {@link ResponseEntity} containing role details.
     * @since 0.1.0
     */
    @GetMapping(Path.ROLE_NAME)
    @Operation(operationId = "getRoleByName", tags = {"roles"},
            summary = "Retrieve one role permissions and activation status", description =
            "Retrieve one role permissions and activation status, by providing its name.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", in = ParameterIn.PATH, description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role permissions and activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RoleResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<RoleResponseDto> getRoleByName(
            @PathVariable String role)
    {
        return ResponseEntity.ok(roleService.getRoleByName(role));
    }

    /**
     * Retrieves the activation status of a specified role for a given microservice. This endpoint is
     * crucial for understanding the operational status of roles across different parts of the system.
     * It allows administrators to query whether a role is active or not within the context of a specified
     * microservice, aiding in managing access control and permissions dynamically.
     *
     * @param role The name of the role whose activation status is being queried.
     * @param microservice The alias of the microservice for which the role's activation status is relevant.
     * @return {@link ResponseEntity} containing the activation status of the role for the specified microservice.
     * @since 1.0.0
     */
    @GetMapping(Path.ROLE_NAME_ACTIVATION)
    @Operation(operationId = "getRoleActivationByRoleAndMicroservice", tags = {"roles"},
            summary = "Retrieve one role activation status for a microservice", description =
            "Retrieve one role activation status for a microservice, " +
                    "by providing its name and the microservice alias.<br>" +
                    "Only available for admins.",
            parameters = {
            @Parameter(name = "role", in = ParameterIn.PATH, description =
                    "The role name"),
            @Parameter(name = "microservice", in = ParameterIn.QUERY, description =
                    "The microservice alias")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleActivationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Role activation status not found for role and microservice provided",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<RoleActivationResponseDto> getRoleActivationByRoleAndMicroservice(
            @PathVariable String role,
            @RequestParam("microservice") String microservice)
    {
        return ResponseEntity.ok(roleService.getRoleActivationByRoleAndMicroservice(role, microservice));
    }

    /**
     * Updates the permissions and/or activation status of a role identified by its name.
     * Allows for comprehensive updates to a role's configuration, including enabling or disabling the role.
     * This operation is restricted to admin users.
     *
     * @param role The name of the role to update.
     * @param roleDto Request DTO containing the new permissions and activation status for the role.
     * @return {@link ResponseEntity} indicating the result of the update operation.
     * @since 0.1.0
     */
    @PutMapping(Path.ROLE_NAME)
    @Operation(operationId = "updateRoleByName", tags = {"roles"},
            summary = "Update a role permissions and / or activation status", description =
            "Update a role permissions and / or activation status, by providing a list of active ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", in = ParameterIn.PATH, description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Role activation status and / or permissions updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Permissions : retrieve a role information for the microservice, " +
                    "to know which one are available.<br>" +
                    "isEnable : provide a boolean to modify value (can be null to keep current value).",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> updateRoleByName(
            @PathVariable String role,
            @Valid @RequestBody RoleUpdateRequestDto roleDto)
    {
        roleService.updateRoleByName(role, roleDto);
        return ResponseEntity.noContent().build();
    }
}
