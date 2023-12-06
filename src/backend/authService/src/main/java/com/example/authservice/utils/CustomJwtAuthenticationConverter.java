package com.example.authservice.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String roleKey;

    public CustomJwtAuthenticationConverter(String roleKey) {
        this.roleKey = roleKey;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String role = jwt.getClaimAsString(roleKey);
        GrantedAuthority authority = new SimpleGrantedAuthority(role.toLowerCase());
        return new JwtAuthenticationToken(jwt, Collections.singletonList(authority));
    }
}

