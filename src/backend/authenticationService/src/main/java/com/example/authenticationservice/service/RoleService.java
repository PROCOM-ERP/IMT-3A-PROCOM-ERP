package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.model.RoleActivation;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleActivationRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import com.example.authenticationservice.utils.RabbitMQSender;
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
    private final LoginProfileRepository loginProfileRepository;
    private final RabbitMQSender rabbitMQSender;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    // private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    /* Public Methods */

    @Transactional
    public String createRole(RoleCreationRequestDto roleCreationRequestDto)
            throws IllegalArgumentException, DataIntegrityViolationException {

        // check if role doesn't already exist
        if (roleRepository.existsById(roleCreationRequestDto.getName()))
            throw new DataIntegrityViolationException("Entity already exists");

        // create new Role entity before database insertion
        Role role = Role.builder()
                .name(roleCreationRequestDto.getName())
                .build();

        // insert Role entity
        Role savedRole = roleRepository.save(role);

        // create RoleActivation entities before database insertion
        Set<RoleActivation> roleActivations = roleCreationRequestDto.getMicroservices().stream()
                .map(m -> RoleActivation.builder()
                        .role(savedRole)
                        .microservice(m)
                        .build())
                .collect(Collectors.toSet());

        // insert RoleActivation entities
        roleActivationRepository.saveAll(roleActivations);

        // return role name
        return savedRole.getName();
    }

    @Transactional
    public void saveAllExternalRoles(List<RoleActivationDto> externalRoleActivations){

        // send message to other services to update all jwt_gen_min_at
        loginProfileRepository.updateAllJwtGenMinAt();
        rabbitMQSender.sendLoginProfilesJwtDisableOldMessage();
    }

    public RolesMicroservicesResponseDto getAllRolesAndMicroservices() {
        return RolesMicroservicesResponseDto.builder()
                .roles(roleRepository.findAllRoleNames())
                .microservices(roleActivationRepository.findAllMicroservices())
                .build();
    }

    public RoleResponseDto getRole(String roleName)
            throws NoSuchElementException {
        Role role = roleRepository.findById(roleName).orElseThrow();
        Set<PermissionDto> permissions = permissionService.getAllPermissions().stream()
                .map(p -> PermissionDto.builder()
                        .name(p)
                        .isEnable(role.getPermissions().contains(p))
                        .build())
                .collect(Collectors.toSet());
        return roleToRoleResponseDto(role, permissions);

    }

    public RoleEnableResponseDto getRoleEnableByMicroservice(String role, String microservice) {
        return roleActivationRepository.findByRoleAndMicroservice(role, microservice)
                .map(ra -> RoleEnableResponseDto.builder()
                        .name(role)
                        .isEnable(ra.getIsEnable())
                        .build())
                .orElse(RoleEnableResponseDto.builder()
                        .name(role)
                        .isEnable(false)
                        .build());
    }

    public List<RoleActivationDto> getAllExternalRoles(@NonNull String getAllRolesPath) {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getAllRolesPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<List<RoleActivationDto>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new NoSuchElementException();

        // return expected external role
        return response.getBody();
    }

    public RoleActivationDto getExternalRole(@NonNull String getRoleByNamePath) {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getRoleByNamePath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<RoleActivationDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new NoSuchElementException();

        // return expected external role
        return response.getBody();
    }

    public void updateRole(String roleName, List<String> permissions)
            throws NoSuchElementException {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElseThrow();
        // set permissions
        role.setPermissions(new LinkedHashSet<>(permissions));
        // save modifications
        roleRepository.save(role);
    }

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

    private RoleResponseDto roleToRoleResponseDto(Role role, Set<PermissionDto> permissions) {
        return RoleResponseDto.builder()
                .name(role.getName())
                .isEnable(isEnableInMicroservice(role))
                .permissions(permissions)
                .build();
    }

}
