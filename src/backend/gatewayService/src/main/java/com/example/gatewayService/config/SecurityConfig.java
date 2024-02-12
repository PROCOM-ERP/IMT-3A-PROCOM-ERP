package com.example.gatewayService.config;

import com.example.gatewayService.model.Endpoint;
import com.example.gatewayService.service.EndpointService;
import com.example.gatewayService.utils.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.List;

@Configuration
@Component("securityConfig")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.jwt.secretkey}")
    private String jwtKey;

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final EndpointService endpointService;

    @Bean
    public SecurityFilterChain jwtfilterChain(HttpSecurity http)
            throws Exception {
        List<Endpoint> endpoints = endpointService.getAllEndpoints();
        return http
                // specify CORS and CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // remove session and cookies in cache
                .sessionManagement(
                        session
                                -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                // permissions
                .authorizeHttpRequests((auth) -> {
                    endpoints.forEach(endpoint -> {
                        HttpMethod httpMethod = endpoint.getHttpMethod();
                        String path = endpoint.getPath();
                        if (endpoint.getPermission() == null) {
                            // logger.info(httpMethod + " " + path + " : PERMIT");
                            auth.requestMatchers(httpMethod, path).permitAll();
                        } else {
                            String permission = endpoint.getPermission().name();
                            // logger.info(httpMethod + " " + path + " : " + permission);
                            auth.requestMatchers(httpMethod, path).hasAuthority(permission);
                        }
                    });
                    auth.anyRequest().permitAll();
                })
                // authentication methods
                .oauth2ResourceServer(
                        (oauth2)
                                -> oauth2.jwt(jwtConfigurer
                                -> jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter)))
                // finalize the build
                .build();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtKey.getBytes(), 0, this.jwtKey.getBytes().length, "HS256");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        jwtDecoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO)); // no clock skew
        return jwtDecoder;
    }

}