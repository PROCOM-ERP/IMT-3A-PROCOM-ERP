package com.example.directoryservice.utils;

import com.example.directoryservice.model.Employee;
import com.example.directoryservice.repository.EmployeeRepository;
import com.example.directoryservice.repository.RoleRepository;
import com.example.directoryservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${security.service.sharedKey}")
    private String sharedKey;
    @Value("${security.service.role}")
    private String serviceRole;
    @Value("${security.jwt.claim.roles}")
    private String jwtClaimRoles;

    private final PermissionService permissionService;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<String> permissions;
        List<String> roles = jwt.getClaimAsStringList(jwtClaimRoles);
        if (Objects.equals(jwt.getSubject(), sharedKey) && roles.contains(serviceRole)) {
            permissions = permissionService.getAllPermissions();
        } else {
            checkTokenValidity(jwt);
            permissions = roleRepository.findDistinctPermissionsByRoleNames(roles);
        }
        return new JwtAuthenticationToken(jwt, permissionsToAuthorities(permissions));
    }

    private void checkTokenValidity(Jwt jwt) throws InsufficientAuthenticationException {
        Employee employee = employeeRepository.findById(jwt.getSubject())
                .orElseThrow(() -> new InsufficientAuthenticationException(""));
        if (! employee.getEnable())
            throw new InsufficientAuthenticationException("");
        Instant jwtMinCreation = employee.getJwtMinCreation()
                .atZone(ZoneId.systemDefault()).toInstant();
        if (jwt.getIssuedAt() == null || jwt.getIssuedAt().isBefore(jwtMinCreation))
            throw new InsufficientAuthenticationException("");
    }

    private List<SimpleGrantedAuthority> permissionsToAuthorities(Set<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }
}