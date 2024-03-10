package com.example.inventoryservice.utils;

import com.example.inventoryservice.model.LoginProfile;
import com.example.inventoryservice.repository.LoginProfileRepository;
import com.example.inventoryservice.repository.RoleRepository;
import com.example.inventoryservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    private final LoginProfileRepository loginProfileRepository;

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

    private void checkTokenValidity(Jwt jwt)
            throws InsufficientAuthenticationException,
            AccessDeniedException
    {
        LoginProfile loginProfile = loginProfileRepository.findById(jwt.getSubject())
                .orElseThrow(() -> new AccessDeniedException(""));
        if (! loginProfile.getIsEnable())
            throw new AccessDeniedException("");
        Instant jwtGenMinAt = loginProfile.getJwtGenMinAt();
        if (jwt.getIssuedAt() == null || jwt.getIssuedAt().isBefore(jwtGenMinAt))
            throw new InsufficientAuthenticationException("Authentication missing or expired.");
    }

    private List<SimpleGrantedAuthority> permissionsToAuthorities(Set<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }
}