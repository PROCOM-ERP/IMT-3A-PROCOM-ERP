package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.RoleRequestDto;
import com.example.authenticationservice.dto.RoleResponseAQMPDto;
import com.example.authenticationservice.dto.RoleResponseDto;
import com.example.authenticationservice.model.Employee;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.model.RoleServiceId;
import com.example.authenticationservice.model.RoleServices;
import com.example.authenticationservice.repository.EmployeeRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.repository.RoleServiceRepository;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import com.example.authenticationservice.utils.RabbitMQSender;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleServiceRepository roleServiceRepository;
    private final PermissionService permissionService;
    private final EmployeeRepository employeeRepository;
    private final RabbitMQSender rabbitMQSender;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    // private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public String createRole(RoleRequestDto roleRequestDto) throws IllegalArgumentException {
        roleRequestDto.getPermissions().forEach(permissionService::isValidPermission);

        Role role = Role.builder()
                .name(roleRequestDto.getName())
                .permissions(new LinkedHashSet<>(roleRequestDto.getPermissions()))
                .build();

        return roleRepository.save(role).getName();
    }

    public void saveAllExternalRoles(List<RoleResponseAQMPDto> roleDtos){
        // get existing roles
        List<Role> existingRoles = roleRepository.findAll();

        // HashMap cast or existing roles
        HashMap<String, Role> rolesHashMap = new HashMap<>();
        existingRoles.forEach(role -> rolesHashMap.put(role.getName(), role));

        // prepare roles to save
        List<Role> rolesToSave = new LinkedList<>();
        roleDtos.forEach(roleDto -> {
            if (roleDto.getEnable())
                rolesToSave.add(createOrUpdateRole(
                        roleDto.getName(),
                        rolesHashMap.get(roleDto.getName())));
        });

        // save
        roleRepository.saveAll(rolesToSave);

        // send message to other services to update all jwt_min_creation
        employeeRepository.updateAllJwtMinCreation();
        rabbitMQSender.sendEmployeesJwtDisableOldMessage();
    }

    public void saveExternalRole(String roleName) {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElse(null);

        // prepare role to save
        role = createOrUpdateRole(roleName, role);

        // save
        roleRepository.save(role);

        // send message to other services to update all jwt_min_creation
        employeeRepository.updateAllJwtMinCreation();
        rabbitMQSender.sendEmployeesJwtDisableOldMessage();
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

    public RoleServices getExternalRole(@NonNull String getRoleByNamePath) {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getRoleByNamePath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<RoleServices> response = restTemplate.exchange(url, HttpMethod.GET,
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

    public void updateRoleServiceEnable(String roleName, String service, Boolean enable) throws NoSuchElementException {
        // check if role exists
        Role role = roleRepository.findById(roleName).orElseThrow();

        // set enable property for matching service
        RoleServiceId roleServiceId = RoleServiceId.builder().role(roleName).service(service).build();
        RoleServices roleService = roleServiceRepository.findById(roleServiceId)
                .map(rs -> {
                    rs.setEnable(enable);
                    return rs;
                })
                .orElse(RoleServices.builder()
                        .role(role)
                        .service(service)
                        .enable(enable)
                        .build());

        // save
        roleServiceRepository.save(roleService);

        // send message to other services to update all jwt_min_creation
        employeeRepository.updateAllJwtMinCreation();
        rabbitMQSender.sendEmployeesJwtDisableOldMessage();
    }

    private Role createOrUpdateRole(String name, Role role) {
        if (role == null)
            return Role.builder().name(name).build();
        else {
            role.setCounter(role.getCounter() + 1);
            role.setEnable(true);
            return role;
        }
    }

    static RoleResponseDto modelToResponseDto(Role role) {
        return RoleResponseDto.builder()
                .name(role.getName())
                .enable(role.getEnable())
                .permissions(role.getPermissions())
                .employees(role.getEmployees().stream()
                        .map(Employee::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

}
