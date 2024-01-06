package com.example.authservice.service;

import com.example.authservice.model.Employee;
import com.example.authservice.model.Role;
import com.example.authservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.duration.minutes}")
    private long duration;

    @Value("${security.jwt.claim.roles}")
    private String jwtClaimRoles;

    private final JwtEncoder jwtEncoder;
    private final EmployeeRepository employeeRepository;

    //private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateJwtToken(Authentication authentication)
            throws InsufficientAuthenticationException, AccessDeniedException {

        // check if employee exists
        Employee employee = employeeRepository.findById(authentication.getName())
                .orElseThrow(() -> new InsufficientAuthenticationException(""));
        // get role names
        List<String> roles =  employee.getRoles().stream().map(Role::getName).toList();
        //logger.info("Roles in Repository : " + roles);
        if (roles.isEmpty()) {
            throw new AccessDeniedException("");
        }

        // generate Jwt token information
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(duration, ChronoUnit.MINUTES)) // termination
                .subject(authentication.getName()) // employee receiving the Jwt token
                .claim(jwtClaimRoles, roles) // put roles into claims section
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}
