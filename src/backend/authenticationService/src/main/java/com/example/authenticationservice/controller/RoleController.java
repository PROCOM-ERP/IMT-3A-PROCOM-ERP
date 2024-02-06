package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.RoleRequestDto;
import com.example.authenticationservice.dto.RoleResponseDto;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.RoleService;
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
@RequestMapping(Path.V1_ROLES)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(operationId = "createRole", tags = {"roles"},
            summary = "Create a new role", description =
            "Create a new role by providing its name and permissions.<br>" +
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
                    "Permissions : retrieve permissions information (permissions section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createRole(@RequestBody RoleRequestDto roleRequestDto) {
        // try to create a new role
        String role = roleService.createRole(roleRequestDto);
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
    @Operation(operationId = "getAllRoles", tags = {"roles"},
            summary = "Retrieve all roles information", description =
            "Retrieve all roles information.<br>" +
            "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Roles information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = RoleResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping(Path.ROLE_NAME)
    @Operation(operationId = "getRole", tags = {"roles"},
            summary = "Retrieve one role information", description =
            "Retrieve one role information, by providing its name.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role information retrieved correctly",
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
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable String role) {
        return ResponseEntity.ok(roleService.getRole(role));
    }

    @PatchMapping(Path.ROLE_NAME_PERMISSIONS)
    @Operation(operationId = "updateRolePermissions", tags = {"roles"},
            summary = "Update a role permissions", description =
            "Update a role permissions, by providing a list of all the new ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "role", description =
            "The role name")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Role permissions updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Permissions : retrieve permissions information (permissions section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateRolePermissions(@PathVariable String role,
                                                        @RequestBody List<String> permissions) {
        roleService.updateRolePermissions(role, permissions);
        return ResponseEntity.noContent().build();
    }
}
