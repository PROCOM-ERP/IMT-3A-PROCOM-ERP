package com.example.directoryservice.service;

import org.springframework.stereotype.Service;
import com.example.directoryservice.model.Permission;

import java.util.Arrays;
import java.util.List;

@Service
public class PermissionService {

    private final List<String> permissions = Arrays.stream(Permission.values())
            .map(Permission::name).toList();


    public List<String> getAllPermissions() {
        return permissions;
    }

    public void isValidPermission(String permission) throws IllegalArgumentException {
        Permission.valueOf(permission);
    }

}
