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

    private final RoleRepository roleRepository;
    private final RoleActivationRepository roleActivationRepository;
    private final PermissionService permissionService;
    private final LoginProfileRepository loginProfileRepository;
    private final RabbitMQSender rabbitMQSender;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    // private final Logger logger = LoggerFactory.getLogger(RoleService.class);

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

        // get all distinct Role entity names from RoleActivation List
        Set<String> roleNames = externalRoleActivations.stream()
                .map(RoleActivationDto::getName)
                .collect(Collectors.toSet());

        // get all existing Role entities and transform the List into a Map
        List<Role> existingRoles = roleRepository.findAllByNameIn(roleNames);
        Map<String, Role> existingRolesMap = existingRoles.stream()
                .collect(Collectors.toMap(Role::getName, r -> r));

        // create new Role entities before database insertion
        List<Role> newRoles = externalRoleActivations.stream()
                .filter(r -> !existingRolesMap.containsKey(r.getName()))
                .map(r -> Role.builder().name(r.getName()).build())
                .toList();

        // insert new Role entities
        roleRepository.saveAll(newRoles).forEach(r -> existingRolesMap.put(r.getName(), r));

        // create new RoleActivation entities before database insertion
        List<RoleActivation> roleActivations = externalRoleActivations.stream()
                .map(ra -> dtoToModel(ra, existingRolesMap.get(ra.getName())))
                .toList();

        // insert new RoleActivation entities
        roleActivationRepository.saveAll(roleActivations);

        // update Role entity isEnable attribute depending on all RoleActivation isEnable values
        List<Role> roles = roleRepository.findAll().stream()
                .peek(this::updateRoleIsEnable)
                .toList();
        roleRepository.saveAll(roles);

        // send message to other services to update all jwt_gen_min_at
        loginProfileRepository.updateAllJwtGenMinAt();
        rabbitMQSender.sendLoginProfilesJwtDisableOldMessage();
    }

    @Transactional
    public void saveExternalRole(RoleActivationDto externalRoleActivation) {
        // check if Role entity exists, and else create it and save it
        Role role = roleRepository.findById(externalRoleActivation.getName())
                .orElse(roleRepository.save(Role.builder()
                        .name(externalRoleActivation.getName())
                        .isEnable(externalRoleActivation.getIsEnable())
                        .build()));

        // insert RoleActivation entity and update Role entity isEnable attribute
        updateRoleActivation(dtoToModel(externalRoleActivation, role), role);
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
        return modelToResponseDto(role, permissions);

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

    public void updateRolePermissions(String roleName, List<String> permissions)
            throws NoSuchElementException {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElseThrow();
        // set permissions
        role.setPermissions(new LinkedHashSet<>(permissions));
        // save modifications
        roleRepository.save(role);
    }

    public void updateRoleActivation(RoleActivationRequestDto roleActivation, String roleName) throws NoSuchElementException {
        // check if Role entity exists
        Role role = roleRepository.findById(roleName).orElseThrow();

        // insert RoleActivation entity and update Role entity isEnable attribute
        updateRoleActivation(requestDtoToModel(roleActivation, role), role);
    }

    private void updateRoleActivation(RoleActivation roleActivation, Role role) {
        // insert RoleActivation entity
        roleActivationRepository.save(roleActivation);

        // update Role entity isEnable attribute depending on all RoleActivation isEnable values
        updateRoleIsEnable(role);
        roleRepository.save(role);

        // send message to other services to update all jwt_gen_min_at
        loginProfileRepository.updateAllJwtGenMinAt();
        rabbitMQSender.sendLoginProfilesJwtDisableOldMessage();
    }

    private RoleActivation dtoToModel(RoleActivationDto ra, Role role) {
        return RoleActivation.builder()
                .role(role)
                .microservice(ra.getMicroservice())
                .isEnable(ra.getIsEnable())
                .build();
    }

    private RoleActivation requestDtoToModel(RoleActivationRequestDto ra, Role role) {
        return RoleActivation.builder()
                .role(role)
                .microservice(ra.getMicroservice())
                .isEnable(ra.getIsEnable())
                .build();
    }

    private void updateRoleIsEnable(Role role) {
        role.setIsEnable(role.getRoleActivations().stream().anyMatch(RoleActivation::getIsEnable));
    }

    static RoleResponseDto modelToResponseDto(Role role, Set<PermissionDto> permissions) {
        return RoleResponseDto.builder()
                .name(role.getName())
                .isEnable(role.getIsEnable())
                .permissions(permissions)
                .build();
    }

}
