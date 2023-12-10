package com.example.authservice.controller;

import com.example.authservice.dto.UserRequestDto;
import com.example.authservice.dto.UserResponseDto;
import com.example.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(operationId = "createUser", tags = {"users"}, summary = "POST New User", description =
            "POST New User by providing password and roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New User created correctly", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Operation failed due to bad request body", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to create the resource", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Operation failed due to internal server error", content = {
                    @Content(mediaType = "application/json")}) })
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto userRequestDto) {
        // create and save the new user and get the response DTO version (or Exceptions)
        Optional<?> savedUser = userService.createUserFromRequestDTO(userRequestDto);

        // check if operation succeed (UserResponseDto), or failed because of user (IllegalArgumentException)
        if (savedUser.isPresent() && ! (savedUser.get() instanceof DataAccessException)) {
            Object objUser = savedUser.get();

            // check if operation succeed (UserResponseDto)
            if (objUser instanceof UserResponseDto userResponseDto) {
                // create URI for the new user with his id
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{idUser}")
                        .buildAndExpand(userResponseDto.getIdUser()).toUri();
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .build();

            // default, it failed because of user (IllegalArgumentException)
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .build();
            }

        // default, operation failed because of the server
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GetMapping
    @Operation(operationId = "getAllUsers", tags = {"users"}, summary = "GET All Users", description =
            "GET All Users, as a list of UserResponseDto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users obtained correctly", content = {
                    @Content(mediaType = "application/json", array =
                    @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)) )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the resource", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Operation failed due to internal server error", content = {
                    @Content(mediaType = "application/json")}) })
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers());
    }

    @GetMapping("/{idUser}")
    @Operation(operationId = "getAllUsers", tags = {"users"}, summary = "GET a User by ID", description =
            "GET a User, as UserResponseDto, by providing its ID in URI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User obtained correctly", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = UserResponseDto.class) )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the resource", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Operation failed due to internal server error", content = {
                    @Content(mediaType = "application/json")}) })
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String idUser) {
        // get user from service
        return userService.getUserById(idUser)
                // if resource exists, create a response with code 200
                .map(userResponseDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(userResponseDto))
                // else, create a response with code 404
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .build());
    }

    @PutMapping("/{idUser}")
    @Operation(operationId = "updateUserById", tags = {"users"}, summary = "UPDATE a User by ID", description =
            "UPDATE a User by providing its ID in URI, and password and / or roles in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated correctly", content = {
                    @Content(mediaType = "application/json", schema =
                    @Schema(implementation = UserResponseDto.class) )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized to update the resource", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Update operation on the resource is forbidden", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Operation failed due to internal server error", content = {
                    @Content(mediaType = "application/json")}) })
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable String idUser, @RequestBody UserRequestDto userRequestDto) {

        // check if the current authenticated user is authorized to modify the resource
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(idUser)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }

        // update user
        return userService.updateUserById(idUser, userRequestDto)
                .map(userResponseDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(userResponseDto))
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build());
    }

}
