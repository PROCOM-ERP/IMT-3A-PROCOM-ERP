package com.example.orderservice.service;

import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.dto.PermissionDto;
import com.example.orderservice.dto.RoleActivationResponseDto;
import com.example.orderservice.dto.RoleResponseDto;
import com.example.orderservice.dto.RoleUpdateRequestDto;
import com.example.orderservice.model.Role;
import com.example.orderservice.repository.RoleRepository;
import com.example.orderservice.utils.CustomHttpRequestBuilder;
import com.example.orderservice.utils.CustomLogger;
import com.example.orderservice.utils.CustomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    /* Constants */
    public static final String ERROR_MSG_ROLE_NAME_BLANK =
            "Role name cannot be null or empty.";
    public static final String ERROR_MSG_ROLE_NAME_SIZE =
            "Role name must contain between 1 and 32 characters.";
    public static final String ERROR_MSG_ROLE_NAME_PATTERN =
            "Role name must start with a letter and can only contain letters, numbers, dashes, and dots. " +
                    "Consecutive special characters are not allowed.";

    @Value("${security.service.name}")
    private String currentMicroservice;

    /* Repository Beans */
    private final RoleRepository roleRepository;

    /* Service Beans */
    private final PermissionService permissionService;
    private final MessageSenderService messageSenderService;

    /* Utils Beans */
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final CustomStringUtils customStringUtils;

    /* Public Methods */

    @LogExecutionTime(description = "Create a new role.",
            tag = CustomLogger.TAG_ROLES)
    public void createRole(String getRoleByNamePath) {
        // retrieve microservice role
        RoleActivationResponseDto roleDto = getMicroserviceRole(getRoleByNamePath);
        // create new Role entity before database insertion
        Role role = Role.builder()
                .name(roleDto.getName())
                .isEnable(roleDto.getIsEnable())
                .build();

        // insert Role entity
        if (! roleRepository.existsById(role.getName()))
            roleRepository.save(role);
    }

    @LogExecutionTime(description = "Retrieve all role names.",
            tag = CustomLogger.TAG_ROLES)
    public Set<RoleActivationResponseDto> getAllRoles() {
        // retrieve all roles
        return roleRepository.findAll().stream()
                .map(this::roleToRoleActivationResponseDto)
                .collect(Collectors.toSet());
    }

    @LogExecutionTime(description = "Retrieve a role.",
            tag = CustomLogger.TAG_ROLES)
    public RoleResponseDto getRoleByName(String roleName)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, CustomStringUtils.REGEX_ROLE_NAME, ERROR_MSG_ROLE_NAME_PATTERN);

        // check if role exists and retrieve it
        Role role = roleRepository.findById(roleName).orElseThrow();

        // set permissions isEnable value
        Set<PermissionDto> permissions = permissionService.getAllPermissions().stream()
                .map(p -> PermissionDto.builder()
                        .name(p)
                        .isEnable(role.getPermissions().contains(p))
                        .build())
                .collect(Collectors.toSet());

        // build and return RoleResponseDto entity
        return RoleResponseDto.builder()
                .isEnable(role.getIsEnable())
                .permissions(permissions)
                .build();
    }

    @LogExecutionTime(description = "Retrieve a role activation status.",
            tag = CustomLogger.TAG_ROLES)
    public RoleActivationResponseDto getRoleActivationByName(String roleName)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, CustomStringUtils.REGEX_ROLE_NAME, ERROR_MSG_ROLE_NAME_PATTERN);

        // retrieve one Role Activation entity
        return roleRepository.findById(roleName)
                .map(this::roleToRoleActivationResponseDto)
                .orElseThrow();
    }

    @Transactional
    @LogExecutionTime(description = "Update a role activation status and / or permissions in this service.",
            tag = CustomLogger.TAG_ROLES)
    public void updateRoleByName(
            String roleName,
            RoleUpdateRequestDto roleDto)
            throws IllegalArgumentException,
            NoSuchElementException,
            DataIntegrityViolationException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, CustomStringUtils.REGEX_ROLE_NAME, ERROR_MSG_ROLE_NAME_PATTERN);

        // check if role already exists and retrieve it
        Role role = roleRepository.findById(roleName).orElseThrow();

        // update isEnable property if provided or different of null
        boolean isEnableChange = false;
        if (roleDto.getIsEnable() != null && roleDto.getIsEnable() != role.getIsEnable()) {
            isEnableChange = true;
            role.setIsEnable(roleDto.getIsEnable());
        }

        // update permissions if permissions are provided
        if (roleDto.getPermissions() != null) {
            // check if permissions are valid
            roleDto.getPermissions().forEach(permissionService::isValidPermission);
            // update permissions and
            role.setPermissions(new LinkedHashSet<>(roleDto.getPermissions()));
        }
        // save all changes
        roleRepository.save(role);

        // send message to inform about a change on role activation status
        if (isEnableChange)
            messageSenderService.sendRoleActivationMessage(roleName);
    }

    /* Private Methods */

    private RoleActivationResponseDto roleToRoleActivationResponseDto(Role role) {
        return RoleActivationResponseDto.builder()
                .name(role.getName())
                .microservice(currentMicroservice)
                .isEnable(role.getIsEnable())
                .build();
    }

    private RoleActivationResponseDto getMicroserviceRole(@NonNull String getRoleByNamePath)
            throws NoSuchElementException, RestClientException {

        // build request
        String url = customHttpRequestBuilder.buildUrl(getRoleByNamePath);
        url = customHttpRequestBuilder.addQueryParamToUrl(url, "microservice", currentMicroservice);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<RoleActivationResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new NoSuchElementException();

        // return expected external role
        return response.getBody();
    }
}