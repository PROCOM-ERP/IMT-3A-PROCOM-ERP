package com.example.authenticationservice.controller;

import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.PermissionService;
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

import java.util.List;

@RestController
@RequestMapping(Path.V1_PERMISSIONS)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(operationId = "getAllPermissions", tags = {"permissions"},
            summary = "Retrieve all permission values", description =
            "Retrieve all permission valus.<br>" +
            "Only available for admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Permission values retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = String.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<String>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

}
