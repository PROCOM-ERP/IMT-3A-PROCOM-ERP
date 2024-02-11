package com.example.authenticationservice.service;

import org.springframework.stereotype.Service;
import com.example.authenticationservice.model.Permission;

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
        Permission.valueOf(permission);
    }

}
