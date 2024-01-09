package com.example.authservice.utils;

import com.example.authservice.repository.EmployeeRepository;
import com.example.authservice.service.RoleService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@AllArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String jwtClaimRoles;
    private final RoleService roleService;
    private final EmployeeRepository employeeRepository;
    //private final Logger logger = LoggerFactory.getLogger(CustomJwtAuthenticationConverter.class);

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Instant jwtMinCreation = employeeRepository.findById(jwt.getSubject())
                .orElseThrow(() -> new InsufficientAuthenticationException("")).getJwtMinCreation()
                .atZone(ZoneId.systemDefault()).toInstant();
        if (jwt.getIssuedAt() == null || jwt.getIssuedAt().isBefore(jwtMinCreation))
            throw new InsufficientAuthenticationException("");
        List<String> roles = jwt.getClaimAsStringList(jwtClaimRoles);
        //logger.info("Roles in Jwt claims : " + roles);
        List<String> permissions = roleService.getRolesPermissions(roles);
        //logger.info("Related permissions : " + permissions);
        return new JwtAuthenticationToken(jwt, permissionsToAuthorities(permissions));
    }

    private List<SimpleGrantedAuthority> permissionsToAuthorities(List<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }
}

