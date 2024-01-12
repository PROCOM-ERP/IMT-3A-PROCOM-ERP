package com.example.directoryservice.service;

import com.example.directoryservice.dto.RoleRequestDto;
import com.example.directoryservice.dto.RoleResponseDto;
import com.example.directoryservice.model.Role;
import com.example.directoryservice.repository.RoleRepository;
import com.example.directoryservice.utils.RabbitMQSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final RabbitMQSender rabbitMQSender;

    // private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public String createRole(RoleRequestDto roleRequestDto) throws IllegalArgumentException {
        roleRequestDto.getPermissions().forEach(permissionService::isValidPermission);

        Role role = Role.builder()
                .name(roleRequestDto.getName())
                .permissions(new LinkedHashSet<>(roleRequestDto.getPermissions()))
                .build();

        String roleName = roleRepository.save(role).getName();

        // send message to auth service to create role
        rabbitMQSender.sendRolesNewMessage(roleName);

        return roleName;
    }

    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleService::modelToResponseDto)
                .toList();
    }

    public RoleResponseDto getRole(String roleName)
            throws NoSuchElementException {
        return roleRepository.findById(roleName)
                .map(RoleService::modelToResponseDto)
                .orElseThrow();
    }

    public List<String> getRolesPermissions(@NonNull List<String> roleNames) {
        return roleRepository.findDistinctPermissionsByRoleNames(roleNames);
    }

    public void updateRolePermissions(String roleName, List<String> permissions)
            throws NoSuchElementException {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElseThrow();
        // set permissions
        role.setPermissions(new LinkedHashSet<>(permissions));
        // save modifications
        roleRepository.save(role);
    }

    public void updateRoleEnable(String roleName, Boolean enable) {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElseThrow();
        // set changes only if enable change
        if (enable != role.getEnable()) {
            // set enable
            role.setEnable(enable);
            // save
            roleRepository.save(role);
            // send message to auth service to update role enable field
            rabbitMQSender.sendRoleEnableModifyMessage(roleName);
        }
    }

    static RoleResponseDto modelToResponseDto(Role role) {
        return RoleResponseDto.builder()
                .name(role.getName())
                .enable(role.getEnable())
                .permissions(role.getPermissions())
                .build();
    }

}