package com.example.authservice.controller;

import com.example.authservice.dto.RoleRequestDto;
import com.example.authservice.model.Path;
import com.example.authservice.model.Role;
import com.example.authservice.service.RoleService;
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
    public ResponseEntity<String> createRole(@RequestBody RoleRequestDto roleRequestDto) {
        String role = roleService.createRole(roleRequestDto).getName();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ROLE)
                .buildAndExpand(role)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }



}
