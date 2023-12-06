package com.example.authservice.controller;

import com.example.authservice.dto.UserRequestDto;
import com.example.authservice.dto.UserResponseDto;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto userRequestDto) {
        // create and save the new user and get the response DTO version
        UserResponseDto savedUser = userService.createUserFromRequestDTO(userRequestDto);
        if (Objects.isNull(savedUser)) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        // create URI for the new user with his id
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idUser}")
                .buildAndExpand(savedUser.getIdUser())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers());
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String idUser) {
        return userService.getUserById(idUser)
                .map(userResponseDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(userResponseDto))
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build());
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable String idUser, @RequestBody UserRequestDto userRequestDto) {

        // check if the user is authorized to modify the resource
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(idUser)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
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
