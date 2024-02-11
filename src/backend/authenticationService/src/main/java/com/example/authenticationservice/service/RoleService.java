package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.model.RoleActivation;
import com.example.authenticationservice.repository.RoleActivationRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import com.example.authenticationservice.utils.PerformanceTracker;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    @Value("${security.service.alias}")
    private String currentMicroservice;

    private final RoleRepository roleRepository;
    private final RoleActivationRepository roleActivationRepository;
    private final PermissionService permissionService;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final RestTemplate restTemplate;

    private final PerformanceTracker performanceTracker;
    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    /* Public Methods */

    @Transactional
    public String createRole(RoleCreationRequestDto roleDto)
            throws IllegalArgumentException, DataIntegrityViolationException {
        logger.info("Start role creation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // check if role doesn't already exist
        if (roleRepository.existsById(roleDto.getName()))
            throw new DataIntegrityViolationException("Entity already exists");

        // create new Role entity before database insertion
        Role role = Role.builder()
                .name(roleDto.getName())
                .build();

        // insert Role entity
        Role savedRole = roleRepository.save(role);

        // create RoleActivation entities before database insertion
        Set<RoleActivation> roleActivations = roleDto.getMicroservices().stream()
                .map(m -> RoleActivation.builder()
                        .role(savedRole)
                        .microservice(m)
                        .build())
                .collect(Collectors.toSet());

        // insert RoleActivation entities
        roleActivationRepository.saveAll(roleActivations);

        // return role name
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to create new role : " + elapsedTimeMillis + " ms");
        return savedRole.getName();
    }

    @Transactional
    public void saveAllMicroserviceRoles(String getAllRolesPath) {
        logger.info("Start external roles saving operation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // retrieve microservice roles
        Set<RoleActivationResponseDto> roleDtos = getAllMicroserviceRoles(getAllRolesPath);

        // retrieve all existing roles
        List<Role> existingRoles = roleRepository.findAll();

        // cast RoleActivationDto entities to RoleActivation entities before database insertion
        Set<RoleActivation> roleActivations = roleDtos.stream()
                .map(ra -> RoleActivation.builder()
                        .role(existingRoles.stream()
                                .filter(r -> ra.getName().equals(r.getName()))
                                .findFirst()
                                .orElseThrow())
                        .microservice(ra.getMicroservice())
                        .isEnable(ra.getIsEnable())
                        .build())
                .collect(Collectors.toSet());

        // insert RoleActivation entities
        roleActivationRepository.saveAllAndFlush(roleActivations);

        // update and insert Role entities global isEnable property
        roleRepository.saveAll(roleRepository.findAll().stream()
                .peek(this::updateRoleIsEnable)
                .toList());
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to save external roles : " + elapsedTimeMillis + " ms");

    }

    @Transactional
    public void saveMicroserviceRole(String getRoleByNamePath)
            throws NoSuchElementException, RestClientException {
        logger.info("Start external role saving operation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // retrieve microservice role
        RoleActivationResponseDto roleDto = getMicroserviceRole(getRoleByNamePath);

        // retrieve Role entity from database
        Role role = roleRepository.findById(roleDto.getName()).orElseThrow();

        // update RoleActivation entity
        RoleActivation roleActivation = RoleActivation.builder()
                .role(role)
                .microservice(roleDto.getMicroservice())
                .isEnable(roleDto.getIsEnable())
                .build();

        // insert RoleActivation entity
        roleActivationRepository.save(roleActivation);

        // update Role entity global isEnable property
        role = roleRepository.findById(roleDto.getName()).orElseThrow();
        updateRoleIsEnable(role);

        // insert Role entity
        roleRepository.save(role);
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to save external role : " + elapsedTimeMillis + " ms");
    }

    public RolesMicroservicesResponseDto getAllRolesAndMicroservices() {
        logger.info("Start retrieving roles and microservices...");
        long startTimeNano = performanceTracker.getCurrentTime();
        RolesMicroservicesResponseDto rolesAndMicroservices = RolesMicroservicesResponseDto.builder()
                .roles(roleRepository.findAllRoleNames())
                .microservices(roleActivationRepository.findAllMicroservices())
                .build();
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve roles and microservices : " + elapsedTimeMillis + " ms");
        return rolesAndMicroservices;
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
                .isEnable(isEnableInMicroservice(role))
                .permissions(permissions)
                .build();

        // return RoleResponseDto entity
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve one role : " + elapsedTimeMillis + " ms");
        return roleDto;

    }

    public RoleActivationResponseDto getRoleActivationByRoleAndMicroservice(String roleName, String microservice) {
        logger.info("Start retrieving one role activation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // check if microservice is not null
        if (microservice == null)
            throw new IllegalArgumentException();

        // retrieve or create (transient) RoleActivationResponseDto entity
        RoleActivationResponseDto roleDto = roleActivationRepository.findByRoleAndMicroservice(roleName, microservice)
                .map(ra -> RoleActivationResponseDto.builder()
                        .name(roleName)
                        .microservice(microservice)
                        .isEnable(ra.getIsEnable())
                        .build())
                .orElse(RoleActivationResponseDto.builder()
                        .name(roleName)
                        .microservice(microservice)
                        .isEnable(false)
                        .build());

        // return RoleActivationResponseDto entity
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to retrieve one role activation : " + elapsedTimeMillis + " ms");
        return roleDto;
    }

    @Transactional
    public void updateRoleByName(String roleName, RoleUpdateRequestDto roleDto)
            throws NoSuchElementException, DataIntegrityViolationException {
        logger.info("Start updating one role...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // update isEnable property if provided or different of null
        if (roleDto.getIsEnable() != null) {
            RoleActivation ra = roleActivationRepository.findByRoleAndMicroservice(roleName, currentMicroservice)
                    .orElse(RoleActivation.builder()
                            // check if role already exists
                            .role(roleRepository.findById(roleName).orElseThrow())
                            .microservice(currentMicroservice)
                            .build());
            ra.setIsEnable(roleDto.getIsEnable());
            roleActivationRepository.saveAndFlush(ra);
        }

        // check if role already exists and retrieve it
        Role role = roleRepository.findById(roleName).orElseThrow();

        // update permissions if permissions are provided
        if (roleDto.getPermissions() != null) {
            // check if permissions are valid
            roleDto.getPermissions().forEach(permissionService::isValidPermission);
            // update permissions and
            role.setPermissions(new LinkedHashSet<>(roleDto.getPermissions()));
        }

        // update global isEnable property if microservice new isEnable property is provided
        if (roleDto.getIsEnable() != null)
            updateRoleIsEnable(role);

        // save all changes
        roleRepository.save(role);
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to update one role : " + elapsedTimeMillis + " ms");
    }

    /* Private Methods */

    private void updateRoleIsEnable(Role role) {
        role.setIsEnable(role.getRoleActivations().stream().anyMatch(RoleActivation::getIsEnable));
    }

    private Boolean isEnableInMicroservice(Role role) {
        for (RoleActivation activation : role.getRoleActivations()) {
            if (activation.getMicroservice().equals(currentMicroservice) && activation.getIsEnable()) {
                return true;
            }
        }
        return false;
    }

    private Set<RoleActivationResponseDto> getAllMicroserviceRoles(@NonNull String getAllRolesPath)
            throws RestClientException {

        // build request
        String url = customHttpRequestBuilder.buildUrl(getAllRolesPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<Set<RoleActivationResponseDto>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new RestClientException("");

        // return expected external role
        return response.getBody();
    }

    private RoleActivationResponseDto getMicroserviceRole(@NonNull String getRoleByNamePath)
            throws RestClientException {

        // build request
        String url = customHttpRequestBuilder.buildUrl(getRoleByNamePath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<RoleActivationResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new RestClientException("");

        // return expected external role
        return response.getBody();
    }
}
