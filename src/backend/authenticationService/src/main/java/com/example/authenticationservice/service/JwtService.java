package com.example.authenticationservice.service;

import com.example.authenticationservice.model.Employee;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@DependsOn("securityConfig")
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.service.sharedKey}")
    private String sharedKey;

    @Value("${security.service.role}")
    private String serviceRole;

    @Value("${security.jwt.duration.minutes}")
    private long duration;

    @Value("${security.jwt.claim.roles}")
    private String jwtClaimRoles;

    private final JwtEncoder jwtEncoder;
    private final EmployeeRepository employeeRepository;

    //private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateJwtToken(String authSubject)
            throws InsufficientAuthenticationException, AccessDeniedException {

        // get roles for auth subject
        List<String> roles = getRolesByAuthSubject(authSubject);

        // generate Jwt token information
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(duration, ChronoUnit.MINUTES)) // termination
                .subject(authSubject) // employee receiving the Jwt token
                .claim(jwtClaimRoles, roles) // put roles into claims section
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    private List<String> getRolesByAuthSubject(String authSubject)
            throws InsufficientAuthenticationException, AccessDeniedException {

        // if service try to connect to another one
        if (Objects.equals(authSubject, sharedKey))
            return Collections.singletonList(serviceRole);

        // check if employee exists
        Employee employee = employeeRepository.findById(authSubject)
                .orElseThrow(() -> new InsufficientAuthenticationException(""));
        // get role names
        List<String> roles =  employee.getRoles().stream()
                .filter(role -> role.getCounter() > 0)
                .map(Role::getName)
                .toList();
        //logger.info("Roles in Repository : " + roles);
        if (roles.isEmpty()) {
            throw new AccessDeniedException("");
        }
        return roles;
    }
}
