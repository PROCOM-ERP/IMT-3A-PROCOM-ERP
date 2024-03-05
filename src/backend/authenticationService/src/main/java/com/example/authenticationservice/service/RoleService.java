package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.model.RoleActivation;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleActivationRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
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
    public String createRole(RoleCreationRequestDto roleDto)
            throws DataIntegrityViolationException
    {
        // sanitize all request parameters
        customStringUtils.sanitizeAllStrings(roleDto);

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
    public void saveAllMicroserviceRoles(String getAllRolesPath)
            throws NoSuchElementException
    {
        // retrieve microservice RoleActivation entities
        Set<RoleActivationResponseDto> roleDtos = getAllMicroserviceRoles(getAllRolesPath);

        // retrieve all existing Role and RoleActivation entities
        List<Role> existingRoles = roleRepository.findAll();
        Map<String, RoleActivation> existingRoleActivations = roleActivationRepository.findAll().stream()
                .collect(Collectors.toMap(
                    ra -> ra.getRole().getName() + "_" + ra.getMicroservice(),
                    ra -> ra));

        // cast RoleActivationDto entities to RoleActivation entities before database insertion
        Set<RoleActivation> roleActivations = new HashSet<>();
        roleDtos.forEach(raDto -> {
            String key = raDto.getName() + "_" + raDto.getMicroservice();
            Role role = existingRoles.stream()
                    .filter(r -> raDto.getName().equals(r.getName()))
                    .findFirst()
                    .orElseThrow(() ->
                            new NoSuchElementException("No existing role named " + raDto.getName()));
            RoleActivation ra;
            if (existingRoleActivations.containsKey(key)) {
                ra = existingRoleActivations.get(key);
                ra.setIsEnable(raDto.getIsEnable());
            } else {
                ra = RoleActivation.builder()
                        .role(role)
                        .microservice(raDto.getMicroservice())
                        .isEnable(raDto.getIsEnable())
                        .build();
            }
            roleActivations.add(ra);
        });

        // insert RoleActivation entities
        roleActivationRepository.saveAllAndFlush(roleActivations);

        // update and insert Role entities global isEnable property
        roleRepository.saveAll(roleRepository.findAll().stream()
                .peek(this::updateRoleIsEnable)
                .toList());

        // expire all Login Profile entities jwt
        loginProfileRepository.updateAllJwtGenMinAt();

        // send message to inform the network about all login profiles jwt expiration
        messageSenderService.sendLoginProfilesJwtDisableOldMessage();
    }

    @Transactional
    public void saveMicroserviceRole(String getRoleByNamePath)
            throws NoSuchElementException,
            RestClientException
    {
        // retrieve microservice role
        RoleActivationResponseDto roleDto = getMicroserviceRole(getRoleByNamePath);

        // retrieve Role entity from database
        Role role = roleRepository.findById(roleDto.getName())
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleDto.getName()));

        // update RoleActivation entity
        RoleActivation roleActivation = roleActivationRepository
                .findByRoleAndMicroservice(roleDto.getName(), roleDto.getMicroservice())
                .orElse(RoleActivation.builder()
                        .role(role)
                        .microservice(roleDto.getMicroservice())
                        .build());
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

        // expire all Login Profile entities jwt
        loginProfileRepository.updateAllJwtGenMinAt();

        // send message to inform the network about all login profiles jwt expiration
        messageSenderService.sendLoginProfilesJwtDisableOldMessage();
    }

    public Set<String> getAllRoleNames()
    {
        return roleRepository.findAllRoleNames();
    }

    public RolesMicroservicesResponseDto getAllRolesAndMicroservices()
    {
        return RolesMicroservicesResponseDto.builder()
                .roles(roleRepository.findAllRoleNames())
                .microservices(roleActivationRepository.findAllMicroservices())
                .build();
    }

    public RoleResponseDto getRoleByName(String roleName)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, ERROR_MSG_ROLE_NAME_PATTERN, CustomStringUtils.REGEX_ROLE_NAME);

        // sanitize all request parameters
        customStringUtils.sanitizeString(roleName);

        // check if role exists and retrieve it
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleName));

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

    public RoleActivationResponseDto getRoleActivationByRoleAndMicroservice(
            String roleName, String microservice)
            throws IllegalArgumentException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, ERROR_MSG_ROLE_NAME_PATTERN, CustomStringUtils.REGEX_ROLE_NAME);

        // check microservice pattern
        customStringUtils.checkNullOrBlankString(microservice, ERROR_MSG_MICROSERVICE_NAME_BLANK);
        customStringUtils.checkStringSize(microservice, ERROR_MSG_MICROSERVICE_NAME_SIZE, 1, 32);

        // sanitize all request parameters
        customStringUtils.sanitizeString(roleName);
        customStringUtils.sanitizeString(microservice);

        // retrieve or create (transient) RoleActivationResponseDto entity and return it
        return roleActivationRepository.findByRoleAndMicroservice(roleName, microservice)
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
    }

    @Transactional
    public void updateRoleByName(String roleName, RoleUpdateRequestDto roleDto)
            throws IllegalArgumentException,
            NoSuchElementException,
            DataIntegrityViolationException
    {
        // check role pattern
        customStringUtils.checkNullOrBlankString(roleName, ERROR_MSG_ROLE_NAME_BLANK);
        customStringUtils.checkStringSize(roleName, ERROR_MSG_ROLE_NAME_SIZE, 1, 32);
        customStringUtils.checkStringPattern(roleName, ERROR_MSG_ROLE_NAME_PATTERN, CustomStringUtils.REGEX_ROLE_NAME);

        // sanitize all request parameters
        customStringUtils.sanitizeString(roleName);
        customStringUtils.sanitizeAllStrings(roleDto);

        // update isEnable property if provided or different of null
        if (roleDto.getIsEnable() != null) {
            RoleActivation ra = roleActivationRepository.findByRoleAndMicroservice(roleName, currentMicroservice)
                    .orElse(RoleActivation.builder()
                            // check if role already exists
                            .role(roleRepository.findById(roleName).orElseThrow(() ->
                                    new NoSuchElementException("No existing role named " + roleName)))
                            .microservice(currentMicroservice)
                            .build());
            ra.setIsEnable(roleDto.getIsEnable());
            roleActivationRepository.saveAndFlush(ra);
        }

        // check if role already exists and retrieve it
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing role named " + roleName));

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
