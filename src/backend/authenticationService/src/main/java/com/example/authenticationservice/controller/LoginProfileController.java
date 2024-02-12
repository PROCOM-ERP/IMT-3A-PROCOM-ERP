package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.LoginProfileActivationResponseDto;
import com.example.authenticationservice.dto.LoginProfileCreationRequestDto;
import com.example.authenticationservice.dto.LoginProfileResponseDto;
import com.example.authenticationservice.dto.LoginProfileUpdateRequestDto;
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

@RestController
@RequestMapping(Path.V1_LOGIN_PROFILES)
@RequiredArgsConstructor
public class LoginProfileController {

    private final LoginProfileService loginProfileService;

    @PostMapping
    @Operation(operationId = "createLoginProfile", tags = {"login-profiles"},
            summary = "Create a new login profile", description =
            "Create a new login profile by providing roles and email address.<br>" +
            "Information about it are available in URI given in the response header location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Login profile created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Email : email standard format.<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createLoginProfile(@RequestBody LoginProfileCreationRequestDto loginProfileCreationRequestDto) {
        // try to create a new loginProfile
        String idLoginProfile = loginProfileService.createLoginProfile(loginProfileCreationRequestDto);
        // generate URI location to inform the client how to get information on the new loginProfile
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.LOGIN_PROFILE_ID)
                .buildAndExpand(idLoginProfile)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping(Path.LOGIN_PROFILE_ID)
    @Operation(operationId = "getLoginProfileById", tags = {"login-profiles"},
            summary = "Retrieve one login profile roles and activations status", description =
            "Retrieve one login profile roles and activations status, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Login profile roles and activations status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<LoginProfileResponseDto> getLoginProfileById(@PathVariable String idLoginProfile) {
        return ResponseEntity.ok(loginProfileService.getLoginProfileById(idLoginProfile));
    }

    @GetMapping(Path.LOGIN_PROFILE_ID_ACTIVATION)
    @Operation(operationId = "getLoginProfileActivationById", tags = {"login-profiles"},
            summary = "Retrieve one login profile activation status", description =
            "Retrieve one login profile activation status, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", description =
                    "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Login profile activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileActivationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<LoginProfileActivationResponseDto> getLoginProfileActivationById(@PathVariable String idLoginProfile) {
        return ResponseEntity.ok(loginProfileService.getLoginProfileActivationById(idLoginProfile));
    }

    @PatchMapping(Path.LOGIN_PROFILE_ID_PASSWORD)
    @Operation(operationId = "updateLoginProfilePasswordById", tags = {"login-profiles"},
            summary = "Update a login profile password", description =
            "Update a login profile password. Only available for the login-profile itself.",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Login-profile password updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "403", description =
                    "Authenticated login-profile cannot update an other login-profile password",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Login-profile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfilePasswordById(@PathVariable String idLoginProfile,
                                                         @RequestBody String password) {
        loginProfileService.updateLoginProfilePasswordById(idLoginProfile, password);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(Path.LOGIN_PROFILE_ID)
    @Operation(operationId = "updateLoginProfileById", tags = {"login-profiles"},
            summary = "Update a login profile roles and activation status", description =
            "Update a login profile roles, by providing a list of all the new ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idLoginProfile", description =
            "The login-profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Login profile activation status and / or roles updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Roles : retrieve a login profile information to know which one are available<br>" +
                    "isEnable : provide a boolean to modify value (can be null to keep current value).",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateLoginProfileById(@PathVariable String idLoginProfile,
                                                          @RequestBody LoginProfileUpdateRequestDto loginProfileDto) {
        loginProfileService.updateLoginProfileById(idLoginProfile, loginProfileDto);
        return ResponseEntity.noContent().build();
    }
}
