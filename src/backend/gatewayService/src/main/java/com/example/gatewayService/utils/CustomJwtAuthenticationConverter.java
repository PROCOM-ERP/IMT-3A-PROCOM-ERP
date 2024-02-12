package com.example.gatewayService.utils;

import com.example.gatewayService.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashSet;
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

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<String> permissions;
        List<String> roles = jwt.getClaimAsStringList(jwtClaimRoles);
        if (Objects.equals(jwt.getSubject(), sharedKey) && roles.contains(serviceRole)) {
            permissions = permissionService.getAllPermissions();
        } else {
            permissions = new LinkedHashSet<>();
        }
        return new JwtAuthenticationToken(jwt, permissionsToAuthorities(permissions));
    }

    private List<SimpleGrantedAuthority> permissionsToAuthorities(Set<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }
}

