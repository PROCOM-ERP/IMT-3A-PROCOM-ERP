package com.example.gatewayService.service;

import com.example.gatewayService.model.Permission;

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

}
