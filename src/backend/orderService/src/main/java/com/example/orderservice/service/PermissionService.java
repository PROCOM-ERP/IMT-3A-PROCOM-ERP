package com.example.orderservice.service;

import com.example.orderservice.model.Permission;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final Set<String> permissions = Arrays.stream(Permission.values())
            .map(Permission::name).collect(Collectors.toSet());


    public Set<String> getAllPermissions() {
        return permissions;
    }

    public void isValidPermission(String permission) throws IllegalArgumentException {
        try {
            Permission.valueOf(permission);
        } catch (IllegalArgumentException ignored) {
            throw new DataIntegrityViolationException("Invalid permissions provided");
        }
    }

}
