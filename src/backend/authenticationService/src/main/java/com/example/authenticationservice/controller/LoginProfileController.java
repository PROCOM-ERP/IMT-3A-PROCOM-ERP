package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.LoginProfileIsEnableMessageDto;
import com.example.authenticationservice.dto.LoginProfileRequestDto;
import com.example.authenticationservice.dto.LoginProfileResponseDto;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.LoginProfileService;
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
@RequestMapping(Path.V1_EMPLOYEES)
@RequiredArgsConstructor
public class LoginProfileController {

    private final LoginProfileService loginProfileService;

    @PostMapping
    @Operation(operationId = "createLoginProfile", tags = {"loginProfiles"},
            summary = "Create a new loginProfile", description =
            "Create a new loginProfile by providing roles and plain text password.<br>" +
            "Information about it are available in URI given in the response header location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "LoginProfile created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createLoginProfile(@RequestBody LoginProfileRequestDto loginProfileRequestDto) {
        // try to create a new loginProfile
        String idLoginProfile = loginProfileService.createLoginProfile(loginProfileRequestDto);
        // generate URI location to inform the client how to get information on the new loginProfile
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.EMPLOYEE_ID)
                .buildAndExpand(idLoginProfile)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(operationId = "getAllLoginProfiles", tags = {"loginProfiles"},
            summary = "Retrieve all loginProfiles information", description =
            "Retrieve all loginProfiles information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "LoginProfiles information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = LoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<LoginProfileResponseDto>> getAllLoginProfiles() {
        return ResponseEntity.ok(loginProfileService.getAllLoginProfiles());
    }

    @GetMapping(Path.EMPLOYEE_ID)
    @Operation(operationId = "getLoginProfile", tags = {"loginProfiles"},
            summary = "Retrieve one loginProfile information", description =
            "Retrieve one loginProfile information, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "LoginProfile information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<LoginProfileResponseDto> getLoginProfile(@PathVariable String idLoginProfile) {
        return ResponseEntity.ok(loginProfileService.getLoginProfile(idLoginProfile));
    }

    @GetMapping(Path.EMPLOYEE_ID_ENABLE)
    @Operation(operationId = "getLoginProfileEnable", tags = {"loginProfiles"},
            summary = "Retrieve one loginProfile information about activation status", description =
            "Retrieve one loginProfile information about activation status, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", description =
                    "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "LoginProfile activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileIsEnableMessageDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<LoginProfileIsEnableMessageDto> getLoginProfileEnable(@PathVariable String idLoginProfile) {
        return ResponseEntity.ok(loginProfileService.getLoginProfileEnable(idLoginProfile));
    }

    @PatchMapping(Path.EMPLOYEE_ID_PASSWORD)
    @Operation(operationId = "updateLoginProfilePassword", tags = {"loginProfiles"},
            summary = "Update an loginProfile password", description =
            "Update an loginProfile password. Only available for the loginProfile itself.",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "LoginProfile password updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "403", description =
                    "Authenticated loginProfile cannot update an other loginProfile password",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfilePassword(@PathVariable String idLoginProfile,
                                                         @RequestBody String password) {
        loginProfileService.updateLoginProfilePassword(idLoginProfile, password);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ROLES)
    @Operation(operationId = "updateLoginProfileRoles", tags = {"loginProfiles"},
            summary = "Update an loginProfile roles", description =
            "Update an loginProfile roles, by providing a list of all the new ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "LoginProfile roles updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfileRoles(@PathVariable String idLoginProfile,
                                                      @RequestBody List<String> roles) {
        loginProfileService.updateLoginProfileRoles(idLoginProfile, roles);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_EMAIL)
    @Operation(operationId = "updateLoginProfileEmail", tags = {"loginProfiles"},
            summary = "Update an loginProfile email", description =
            "Update an loginProfile email, by providing the new one.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "LoginProfile email updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Email : 63 characters for 'username', @, 63 for subdomain, and the rest for TLD. Maximum 320 characters.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfileEmail(@PathVariable String idLoginProfile,
                                                       @RequestBody String email) {
        loginProfileService.updateLoginProfileEmail(idLoginProfile, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ENABLE)
    @Operation(operationId = "updateLoginProfileEnable", tags = {"loginProfiles"},
            summary = "Enable or disable an loginProfile", description =
            "Enable or disable an loginProfile, by providing a new enable value (true or false).<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idLoginProfile", description =
                    "The loginProfile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "LoginProfile enable attribute updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "LoginProfile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Enable : boolean value (true or false).",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfileEnable(@PathVariable String idLoginProfile,
                                                       @RequestBody Boolean enable) {
        loginProfileService.updateLoginProfileEnable(idLoginProfile, enable);
        return ResponseEntity.noContent().build();
    }

}
