package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.service.LoginProfileService;
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

/**
 * Controller responsible for managing User LoginProfiles for security purposes.
 * A User's LoginProfile includes roles and an activation status, critical for authentication and authorization processes.
 * This controller provides endpoints for creating, retrieving, updating LoginProfiles and their specific attributes such as passwords and activation status.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 0.1.0 (2024-01-15)
 */
@RestController
@RequestMapping(Path.V1_LOGIN_PROFILES)
@RequiredArgsConstructor
public class LoginProfileController {

    /* Service Beans */

    /**
     * Service for managing login profiles. This includes operations such as creating, retrieving,
     * updating, and deleting user login profiles. The service layer abstracts the business logic
     * associated with user authentication and authorization, including handling roles, activation
     * status, and password management.
     *
     * @since 0.1.0
     */
    private final LoginProfileService loginProfileService;

    /* Endpoints Methods */

    /**
     * Creates a new LoginProfile by accepting roles and email address as input.
     * The endpoint responds with the URI of the newly created LoginProfile in the 'Location' header of the response.
     *
     * @param loginProfileCreationRequestDto DTO containing the new LoginProfile's details.
     * @return {@link ResponseEntity} with the creation status and the URI of the new LoginProfile.
     * @throws Exception General exception for handling unexpected errors.
     * @since 0.1.0
     */
    @PostMapping
    @Operation(operationId = "createLoginProfile", tags = {"login-profiles"},
            summary = "Create a new login profile", description =
            "Create a new login profile by providing roles and email address.<br>" +
            "Information about it are available in URI given in the response header location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Login profile created correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileIdResponseDto.class)) }),
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
                    "Email : email standard format.<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<LoginProfileIdResponseDto> createLoginProfile(
            @Valid @RequestBody LoginProfileCreationRequestDto loginProfileCreationRequestDto)
            throws Exception
    {
        // try to create a new loginProfile
        LoginProfileIdResponseDto loginProfileIdResponseDto = loginProfileService
                .createLoginProfile(loginProfileCreationRequestDto);
        // generate URI location to inform the client how to get information on the new loginProfile
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.LOGIN_PROFILE_ID)
                .buildAndExpand(loginProfileIdResponseDto.getId())
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).body(loginProfileIdResponseDto);
    }

    /**
     * Retrieves the roles and activation status of a LoginProfile identified by its username (id).
     *
     * @param idLoginProfile The username (ID) of the LoginProfile.
     * @return {@link ResponseEntity} containing the roles and activation status of the LoginProfile.
     * @since 0.1.0
     */
    @GetMapping(Path.LOGIN_PROFILE_ID)
    @Operation(operationId = "getLoginProfileById", tags = {"login-profiles"},
            summary = "Retrieve one login profile roles and activations status", description =
            "Retrieve one login profile roles and activations status, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", in = ParameterIn.PATH, description =
            "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Login profile roles and activations status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<LoginProfileResponseDto> getLoginProfileById(
            @PathVariable String idLoginProfile)
    {
        return ResponseEntity.ok(loginProfileService.getLoginProfileById(idLoginProfile));
    }

    /**
     * Retrieves the activation status of a LoginProfile by its username (ID).
     *
     * @param idLoginProfile The username (ID) of the LoginProfile.
     * @return {@link ResponseEntity} containing the activation status of the LoginProfile.
     * @since 0.1.0
     */
    @GetMapping(Path.LOGIN_PROFILE_ID_ACTIVATION)
    @Operation(operationId = "getLoginProfileActivationById", tags = {"login-profiles"},
            summary = "Retrieve one login profile activation status", description =
            "Retrieve one login profile activation status, by providing its id (username).",
            parameters = {@Parameter(name = "idLoginProfile", in = ParameterIn.PATH, description =
                    "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Login profile activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginProfileActivationResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<LoginProfileActivationResponseDto> getLoginProfileActivationById(
            @PathVariable String idLoginProfile)
    {
        return ResponseEntity.ok(loginProfileService.getLoginProfileActivationById(idLoginProfile));
    }

    /**
     * Updates the password for a LoginProfile identified by its username (ID).
     * This endpoint is intended for the LoginProfile itself to update its password.
     *
     * @param idLoginProfile The username (ID) of the LoginProfile.
     * @param passwordDto DTO containing the new password details.
     * @return {@link ResponseEntity} with no content on successful update.
     * @since 0.1.0
     */
    @PatchMapping(Path.LOGIN_PROFILE_ID_PASSWORD)
    @Operation(operationId = "updateLoginProfilePasswordById", tags = {"login-profiles"},
            summary = "Update a login profile password", description =
            "Update a login profile password. Only available for the login-profile itself.",
            parameters = {@Parameter(name = "idLoginProfile", in = ParameterIn.PATH, description =
            "The login profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Login-profile password updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "403", description =
                    "Authenticated login-profile cannot update an other login-profile password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Login-profile not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> updateLoginProfilePasswordById(
            @PathVariable String idLoginProfile,
            @Valid @RequestBody LoginProfilePasswordUpdateRequestDto passwordDto)
    {
        loginProfileService.updateLoginProfilePasswordById(idLoginProfile, passwordDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the roles and activation status of a LoginProfile identified by its username (ID).
     * This endpoint is primarily intended for admin use, allowing for the update of roles and the activation status.
     *
     * @param idLoginProfile The username (ID) of the LoginProfile.
     * @param loginProfileDto DTO containing the updated roles and activation status.
     * @return {@link ResponseEntity} with no content on successful update.
     * @since 0.1.0
     */
    @PutMapping(Path.LOGIN_PROFILE_ID)
    @Operation(operationId = "updateLoginProfileById", tags = {"login-profiles"},
            summary = "Update a login profile roles and activation status", description =
            "Update a login profile roles, by providing a list of all the new ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idLoginProfile", in = ParameterIn.PATH, description =
            "The login-profile username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Login profile activation status and / or roles updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Login profile not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Roles : retrieve a login profile information to know which one are available<br>" +
                    "isEnable : provide a boolean to modify value (can be null to keep current value).",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> updateLoginProfileById(
            @PathVariable String idLoginProfile,
            @Valid @RequestBody LoginProfileUpdateRequestDto loginProfileDto)
    {
        loginProfileService.updateLoginProfileById(idLoginProfile, loginProfileDto);
        return ResponseEntity.noContent().build();
    }
}
