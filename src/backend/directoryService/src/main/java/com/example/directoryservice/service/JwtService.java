package com.example.directoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
            throws AccessDeniedException {

        // if service try to connect to another one
        if (Objects.equals(authSubject, sharedKey))
            return Collections.singletonList(serviceRole);
        throw new AccessDeniedException("");

    }
}
