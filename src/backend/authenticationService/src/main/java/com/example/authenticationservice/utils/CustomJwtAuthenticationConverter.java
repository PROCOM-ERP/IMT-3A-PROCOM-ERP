package com.example.authenticationservice.utils;

import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.service.PermissionService;
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
    private final LoginProfileRepository employeeRepository;

    //private final Logger logger = LoggerFactory.getLogger(CustomJwtAuthenticationConverter.class);

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> permissions;
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
        Instant jwtMinCreation = employeeRepository.findById(jwt.getSubject())
                .orElseThrow(() -> new InsufficientAuthenticationException("")).getJwtGenMinAt();
        if (jwt.getIssuedAt() == null || jwt.getIssuedAt().isBefore(jwtMinCreation))
            throw new InsufficientAuthenticationException("");
    }

    private List<SimpleGrantedAuthority> permissionsToAuthorities(List<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }
}

