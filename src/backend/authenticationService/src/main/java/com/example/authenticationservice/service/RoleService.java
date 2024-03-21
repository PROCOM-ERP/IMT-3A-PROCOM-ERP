package com.example.authenticationservice.service;

import com.example.authenticationservice.annotation.LogExecutionTime;
import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.model.RoleActivation;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleActivationRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import com.example.authenticationservice.utils.CustomLogger;
import com.example.authenticationservice.utils.CustomStringUtils;
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

import java.util.*;
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
    public static final String ERROR_MSG_MICROSERVICE_NAME_BLANK =
            "Microservice name cannot be null or blank.";
    public static final String ERROR_MSG_MICROSERVICE_NAME_SIZE =
            "Microservice name must contain between 1 and 32 characters.";

    @Value("${security.service.name}")
    private static String currentMicroservice;

    /* Repository Beans */
    private final LoginProfileRepository loginProfileRepository;
    private final RoleActivationRepository roleActivationRepository;
    private final RoleRepository roleRepository;

    /* Service Beans */
    private final MessageSenderService messageSenderService;
    private final PermissionService permissionService;

    /* Utils Beans */
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final CustomStringUtils customStringUtils;
    private final RestTemplate restTemplate;

    /* Public Methods */

    @Transactional
    @LogExecutionTime(description = "Create a new role.",
            tag = CustomLogger.TAG_ROLES)
    public String createRole(RoleCreationRequestDto roleDto)
            throws DataIntegrityViolationException
    {
        // sanitize all request parameters
        roleDto.setName(customStringUtils.sanitizeString(roleDto.getName()));
        roleDto.setMicroservices(roleDto.getMicroservices().stream()
                .map(customStringUtils::sanitizeString)
                .collect(Collectors.toSet()));

        // check if role doesn't already exist
        if (roleRepository.existsById(roleDto.getName()))
            throw new DataIntegrityViolationException("Role with the provided name already exists.");

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

        // send message to inform the network about role creation
        messageSenderService.sendRolesNewMessage(savedRole.getName());

        // return role name
        return savedRole.getName();
    }

    @Transactional
    @LogExecutionTime(description = "Save all roles from a microservice.",
            tag = CustomLogger.TAG_ROLES)
    public void saveAllMicroserviceRoles(String getAllRolesPath)
            throws NoSuchElementException
    {
        // retrieve microservice RoleActivation entities
        Set<RoleActivationResponseDto> roleDtos = getAllMicroserviceRoles(getAllRolesPath);

        // check if Role entities exists, else create them
        List<Role> existingRoles = roleRepository.findAll();
        List<Role> nonExistingRoles = roleDtos.stream()
                .map(raDto -> Role.builder().name(raDto.getName()).build())
                .filter(r -> ! existingRoles.contains(r))
                .toList();
        if (! nonExistingRoles.isEmpty()) {
            roleRepository.saveAll(nonExistingRoles);
            nonExistingRoles.forEach(r -> messageSenderService.sendRolesNewMessage(r.getName()));
        }

        // cast RoleActivationDto entities to RoleActivation entities before database insertion
        Map<String, Role> roles = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getName, r -> r));
        Map<String, RoleActivation> existingRoleActivations = roleActivationRepository.findAll().stream()
                .collect(Collectors.toMap(
                        ra -> ra.getRole().getName() + "_" + ra.getMicroservice(),
                        ra -> ra));
        Set<RoleActivation> roleActivations = roleDtos.stream()
                .map(raDto -> {
                    String key = raDto.getName() + "_" + raDto.getMicroservice();
                    RoleActivation ra = existingRoleActivations.get(key);
                    if (ra != null) {
                        ra.setIsEnable(raDto.getIsEnable());
                    } else {
                        ra = RoleActivation.builder()
                                .role(roles.get(raDto.getName()))
                                .microservice(raDto.getMicroservice())
                                .isEnable(raDto.getIsEnable())
                                .build();
                        existingRoleActivations.put(key, ra);
                    }
                    return ra;
                }).collect(Collectors.toSet());

        // insert RoleActivation entities
        roleActivationRepository.saveAll(roleActivations);

        // update and insert Role entities global isEnable property
        roleRepository.saveAll(roleRepository.findAll().stream()
                .peek(this::updateRoleIsEnable)
                .collect(Collectors.toSet()));

        // expire all Login Profile entities jwt
        loginProfileRepository.updateAllJwtGenMinAt();

        // send message to inform the network about all login profiles jwt expiration
        messageSenderService.sendLoginProfilesJwtDisableOldMessage();
    }

    @Transactional
    @LogExecutionTime(description = "Save a role from a microservice.",
            tag = CustomLogger.TAG_ROLES)
    public void saveMicroserviceRole(String getRoleByNamePath)
            throws NoSuchElementException,
            RestClientException
    {
        // retrieve microservice role
        RoleActivationResponseDto roleDto = getMicroserviceRole(getRoleByNamePath);

        // retrieve Role entity from database
        Role role = roleRepository.findById(roleDto.getName())
                .orElse(roleRepository.save(Role.builder()
                        .name(roleDto.getName())
                        .isEnable(roleDto.getIsEnable())
                        .build()));

        // update RoleActivation entity
        RoleActivation roleActivation = roleActivationRepository
                .findByRoleAndMicroservice(roleDto.getName(), roleDto.getMicroservice())
                .orElse(RoleActivation.builder()
                        .role(role)
                        .microservice(roleDto.getMicroservice())
                        .isEnable(roleDto.getIsEnable())
                        .build());
        boolean isEnableChange = roleDto.getIsEnable() != roleActivation.getIsEnable();
        roleActivation.setIsEnable(roleDto.getIsEnable());

        // insert RoleActivation entity
        roleActivationRepository.save(roleActivation);

        // update Role entity global isEnable property
        role = roleRepository.findById(roleDto.getName())
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleDto.getName()));
        updateRoleIsEnable(role);

        // insert Role entity
        roleRepository.save(role);

        // check if role activation status has changed before resetting all user Jwt tokens
        if (isEnableChange) {
            // expire all Login Profile entities jwt
            loginProfileRepository.updateAllJwtGenMinAt();
            // send message to inform the network about all login profiles jwt expiration
            messageSenderService.sendLoginProfilesJwtDisableOldMessage();
        }
    }

    @LogExecutionTime(description = "Retrieve all role names.",
            tag = CustomLogger.TAG_ROLES)
    public Set<String> getAllRoleNames()
    {
        return roleRepository.findAllRoleNames();
    }

    @LogExecutionTime(description = "Retrieve all role and microservice names.",
            tag = CustomLogger.TAG_ROLES)
    public RolesMicroservicesResponseDto getAllRolesAndMicroservices()
    {
        return RolesMicroservicesResponseDto.builder()
                .roles(roleRepository.findAllRoleNames())
                .microservices(roleActivationRepository.findAllMicroservices())
                .build();
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

        // sanitize all request parameters
        String roleNameSanitized = customStringUtils.sanitizeString(roleName);

        // check if role exists and retrieve it
        Role role = roleRepository.findById(roleNameSanitized)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleNameSanitized));

        // set permissions isEnable value
        Set<PermissionDto> permissions = permissionService.getAllPermissions().stream()
                .map(p -> PermissionDto.builder()
                        .name(p)
                        .isEnable(role.getPermissions().contains(p))
                        .build())
                .collect(Collectors.toSet());

        // build and return RoleResponseDto entity
        return RoleResponseDto.builder()
                .isEnable(isEnableInMicroservice(role))
                .permissions(permissions)
                .build();
    }

    @LogExecutionTime(description = "Retrieve a role activation status for a specific microservice.",
            tag = CustomLogger.TAG_ROLES)
    public RoleActivationResponseDto getRoleActivationByRoleAndMicroservice(
            String roleName, String microservice)
            throws IllegalArgumentException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, CustomStringUtils.REGEX_ROLE_NAME, ERROR_MSG_ROLE_NAME_PATTERN);

        // check microservice pattern
        customStringUtils.checkNullOrBlankString(microservice, ERROR_MSG_MICROSERVICE_NAME_BLANK);
        customStringUtils.checkStringSize(microservice, ERROR_MSG_MICROSERVICE_NAME_SIZE, 1, 32);

        // sanitize all request parameters
        String roleNameSanitized = customStringUtils.sanitizeString(roleName);
        String microserviceSanitized = customStringUtils.sanitizeString(microservice);

        // retrieve or create (transient) RoleActivationResponseDto entity and return it
        return roleActivationRepository.findByRoleAndMicroservice(roleNameSanitized, microserviceSanitized)
                .map(ra -> RoleActivationResponseDto.builder()
                        .name(roleNameSanitized)
                        .microservice(microserviceSanitized)
                        .isEnable(ra.getIsEnable())
                        .build())
                .orElse(RoleActivationResponseDto.builder()
                        .name(roleNameSanitized)
                        .microservice(microserviceSanitized)
                        .isEnable(false)
                        .build());
    }

    @Transactional
    @LogExecutionTime(description = "Update a role activation status and / or permissions in this service.",
            tag = CustomLogger.TAG_ROLES)
    public void updateRoleByName(String roleName, RoleUpdateRequestDto roleDto)
            throws IllegalArgumentException,
            NoSuchElementException,
            DataIntegrityViolationException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, CustomStringUtils.REGEX_ROLE_NAME, ERROR_MSG_ROLE_NAME_PATTERN);

        // sanitize all request parameters
        String roleNameSanitized = customStringUtils.sanitizeString(roleName);
        roleDto.setPermissions(roleDto.getPermissions().stream()
                .map(customStringUtils::sanitizeString)
                .collect(Collectors.toSet()));

        // update isEnable property if provided or different of null
        boolean isEnableChange = false;
        if (roleDto.getIsEnable() != null) {
            RoleActivation ra = roleActivationRepository.findByRoleAndMicroservice(roleNameSanitized, currentMicroservice)
                    .orElse(RoleActivation.builder()
                            // check if role already exists
                            .role(roleRepository.findById(roleNameSanitized).orElseThrow(() ->
                                    new NoSuchElementException("No existing role named " + roleNameSanitized)))
                            .microservice(currentMicroservice)
                            .build());
            if (roleDto.getIsEnable() != ra.getIsEnable()) {
                isEnableChange = true;
                ra.setIsEnable(roleDto.getIsEnable());
                roleActivationRepository.saveAndFlush(ra);
            }
        }

        // check if role already exists and retrieve it
        Role role = roleRepository.findById(roleNameSanitized)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleNameSanitized));

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

        // expire all Login Profile entities jwt
        loginProfileRepository.updateAllJwtGenMinAt();

        // send message to inform the network about all login profiles jwt expiration
        if (isEnableChange)
            messageSenderService.sendLoginProfilesJwtDisableOldMessage();
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
