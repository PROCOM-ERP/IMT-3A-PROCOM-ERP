package com.example.directoryservice.service;

import com.example.directoryservice.dto.PermissionDto;
import com.example.directoryservice.dto.RoleActivationResponseDto;
import com.example.directoryservice.dto.RoleResponseDto;
import com.example.directoryservice.dto.RoleUpdateRequestDto;
import com.example.directoryservice.model.Role;
import com.example.directoryservice.repository.RoleRepository;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import com.example.directoryservice.utils.PerformanceTracker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${security.service.name}")
    private String currentMicroservice;

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final MessageSenderService messageSenderService;

    private final PerformanceTracker performanceTracker;
    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    /* Public Methods */

    public void createRole(String getRoleByNamePath) {
        logger.info("Start role creation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        try {
            // retrieve microservice role
            RoleActivationResponseDto roleDto = getMicroserviceRole(getRoleByNamePath);
            // create new Role entity before database insertion
            Role role = Role.builder()
                    .name(roleDto.getName())
                    .isEnable(roleDto.getIsEnable())
                    .build();

            // insert Role entity
            roleRepository.save(role);
        } catch (Exception e) {
            logger.error("Something went wrong with role creation : " + e.getCause());
        }

        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to create new role : " + elapsedTimeMillis + " ms");
    }

    public Set<RoleActivationResponseDto> getAllRoles() {
        logger.info("Start retrieving roles...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // retrieve all roles
        Set<RoleActivationResponseDto> roles = roleRepository.findAll().stream()
                .map(this::roleToRoleActivationResponseDto)
                .collect(Collectors.toSet());

        // return all roles
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve roles and microservices : " + elapsedTimeMillis + " ms");
        return roles;
    }

    public RoleResponseDto getRoleByName(String roleName)
            throws NoSuchElementException {
        logger.info("Start retrieving one role...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // check if role exists and retrieve it
        Role role = roleRepository.findById(roleName).orElseThrow();

        // set permissions isEnable value
        Set<PermissionDto> permissions = permissionService.getAllPermissions().stream()
                .map(p -> PermissionDto.builder()
                        .name(p)
                        .isEnable(role.getPermissions().contains(p))
                        .build())
                .collect(Collectors.toSet());

        // build RoleResponseDto entity
        RoleResponseDto roleDto = RoleResponseDto.builder()
                .isEnable(role.getIsEnable())
                .permissions(permissions)
                .build();

        // return RoleResponseDto entity
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve one role : " + elapsedTimeMillis + " ms");
        return roleDto;
    }

    public RoleActivationResponseDto getRoleActivationByName(String roleName)
            throws NoSuchElementException {
        logger.info("Start retrieving one role activation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // retrieve one Role Activation entity
        RoleActivationResponseDto roleDto = roleRepository.findById(roleName)
                .map(this::roleToRoleActivationResponseDto)
                .orElseThrow();

        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve one role activation : " + elapsedTimeMillis + " ms");
        return roleDto;
    }

    @Transactional
    public void updateRoleByName(String roleName, RoleUpdateRequestDto roleDto)
            throws NoSuchElementException, DataIntegrityViolationException {
        logger.info("Start updating one role...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // check if role already exists and retrieve it
        Role role = roleRepository.findById(roleName).orElseThrow();

        // update isEnable property if provided or different of null
        if (roleDto.getIsEnable() != null) {
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
        if (roleDto.getIsEnable() != null)
            messageSenderService.sendRoleActivationMessage(roleName);
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to update one role : " + elapsedTimeMillis + " ms");
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