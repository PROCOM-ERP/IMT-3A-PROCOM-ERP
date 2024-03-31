package com.example.directoryservice.controller;

import com.example.directoryservice.dto.HttpStatusErrorDto;
import com.example.directoryservice.dto.RoleActivationResponseDto;
import com.example.directoryservice.dto.RoleResponseDto;
import com.example.directoryservice.dto.RoleUpdateRequestDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.RoleService;
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
import java.util.Set;

@RestController
@RequestMapping(Path.V1_ROLES)
@RequiredArgsConstructor
public class RoleController {

    /* Service Beans */

    private final RoleService roleService;

    /* Endpoints Methods */

    @GetMapping
    @Operation(operationId = "getAllRoles", tags = {"roles"},
            summary = "Retrieve all roles", description =
            "Retrieve all roles.<br>" +
            "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Roles information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = RoleActivationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<Set<RoleActivationResponseDto>> getAllRoles()
    {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping(Path.ROLE_NAME)
    @Operation(operationId = "getRolebyName", tags = {"roles"},
            summary = "Retrieve one role permissions and activation status", description =
            "Retrieve one role permissions and activation status, by providing its name.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", in = ParameterIn.PATH, description =
                    "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role information retrieved correctly",
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

    @GetMapping(Path.ROLE_NAME_ACTIVATION)
    @Operation(operationId = "getRoleActivationByName", tags = {"roles"},
            summary = "Retrieve one role activation status for a microservice", description =
            "Retrieve one role activation status for a microservice, by providing its name.<br>" +
                    "Only available for admins.",
            parameters = {@Parameter(name = "role", in = ParameterIn.PATH, description =
                            "The role name")})
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
                    "Role activation status not found for role provided",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<RoleActivationResponseDto> getRoleActivationByName(
            @PathVariable String role)
    {
        return ResponseEntity.ok(roleService.getRoleActivationByName(role));
    }

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
                    "Role permissions and / or activation status updated correctly",
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
