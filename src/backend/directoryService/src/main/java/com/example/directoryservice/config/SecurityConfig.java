package com.example.directoryservice.config;

import com.example.directoryservice.model.Endpoint;
import com.example.directoryservice.service.EndpointService;
import com.example.directoryservice.service.RoleService;
import com.example.directoryservice.utils.CustomJwtAuthenticationConverter;
import com.example.directoryservice.utils.SharedKeyAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.services.sharedkey}") private String sharedKey;

    @Value("${security.jwt.secretkey}")
    private String jwtKey;

    @Value("${security.jwt.claim.roles}")
    private String jwtClaimRoles;

    private final EndpointService endpointService;
    private final RoleService roleService;

    //private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain jwtfilterChain(HttpSecurity http) throws Exception {
        List<Endpoint> endpoints = endpointService.getAllEndpoints();
        return http
                // specify CORS and CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // remove session and cookies in cache
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                // permissions
                .authorizeHttpRequests((auth) -> {
                    endpoints.forEach(endpoint -> {
                        HttpMethod httpMethod = endpoint.getHttpMethod();
                        String path = endpoint.getPath();
                        if (endpoint.getPermission() == null) {
                            //logger.info(httpMethod + " " + path + " : PERMIT");
                            auth.requestMatchers(httpMethod, path).permitAll();
                        } else {
                            String permission = endpoint.getPermission().name();
                            //logger.info(httpMethod + " " + path + " : " + permission);
                            auth.requestMatchers(httpMethod, path).hasAuthority(permission);
                        }
                    });
                    auth.anyRequest().authenticated();
                })
                // authentication method
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter(jwtClaimRoles, roleService))))
                // finalize the build
                .addFilterBefore(new SharedKeyAuthenticationFilter(sharedKey, endpointService),
                        BearerTokenAuthenticationFilter.class)
                .build();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtKey.getBytes(), 0, this.jwtKey.getBytes().length,"RSA");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        jwtDecoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO)); // no clock skew
        return jwtDecoder;
    }

}
