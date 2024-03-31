package com.example.authenticationservice.config;

import com.example.authenticationservice.model.Endpoint;
import com.example.authenticationservice.service.CustomUserDetailsService;
import com.example.authenticationservice.service.EndpointService;
import com.example.authenticationservice.utils.CustomAccessDeniedHandler;
import com.example.authenticationservice.utils.CustomJwtAuthenticationConverter;

import java.time.Duration;
import java.util.List;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuration class for Spring Security setup in the application. It defines the security configurations for basic authentication and JWT-based authentication.
 * The configuration controls aspects like CORS, CSRF protection, session management, request caching, authorization rules based on HTTP methods and paths,
 * and custom JWT validation. It leverages {@link CustomUserDetailsService} and {@link EndpointService} for dynamic endpoint-based security and
 * utilizes {@link CustomJwtAuthenticationConverter} and {@link CustomAccessDeniedHandler} for JWT authentication conversion and handling access denied errors respectively.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.1.0 (2024-03-31)
 */
@Configuration
@Component("securityConfig")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The secret key used for JWT token generation and validation. This key is defined in the application properties.
     *
     * @since 0.1.0
     */
    @Value("${security.jwt.secretkey}")
    private String jwtKey;

    /* Service Beans */

    /**
     * A service to load user-specific data. It is used by the authentication manager to authenticate a user.
     *
     * @since 0.1.0
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * A service to access the application's endpoints configuration. It allows dynamic security configuration based on the endpoints' properties.
     *
     * @since 0.1.0
     */
    private final EndpointService endpointService;

    /* Utils Beans */

    /**
     * A converter to process JWT tokens for authentication. It converts a JWT token into an authenticated user principle.
     *
     * @since 0.1.0
     */
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    /**
     * A handler for access denied exceptions. It provides custom responses when an authenticated user is not authorized to access certain resources.
     *
     * @since 1.1.0
     */
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Configures a {@link SecurityFilterChain} specifically for basic authentication, primarily for the API authentication endpoints.
     * This method demonstrates how to customize authentication and authorization using HTTP basic authentication.
     *
     * @param http The {@link HttpSecurity} to configure
     * @return The {@link SecurityFilterChain} object after configuration
     * @throws Exception if an error occurs during configuration
     * @since 0.1.0
     */
    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .securityMatcher("api/v1/auth/**")
                // specify CORS and CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // remove session and cookies in cache
                .sessionManagement(
                        session
                                -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                // permissions
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                // authentication methods
                .httpBasic(Customizer.withDefaults())
                // finalize the build
                .build();
    }

    /**
     * Configures a {@link SecurityFilterChain} for JWT token-based authentication. It sets up authorization rules,
     * session management, CSRF protection, and configures the OAuth2 resource server with a custom JWT converter.
     *
     * @param http The {@link HttpSecurity} to configure
     * @return The {@link SecurityFilterChain} object after configuration
     * @throws Exception if an error occurs during configuration
     * @since 0.1.0
     */
    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http)
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
                            auth.requestMatchers(httpMethod, path).permitAll();
                        } else {
                            String permission = endpoint.getPermission().name();
                            auth.requestMatchers(httpMethod, path).hasAuthority(permission);
                        }
                    });
                    auth.anyRequest().authenticated();
                })
                // authentication methods
                .oauth2ResourceServer(
                        (oauth2)
                                -> oauth2.jwt(jwtConfigurer
                                -> jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter)))
                // handle AccessDeniedException
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // finalize the build
                .build();
    }

    /**
     * Provides a {@link GrantedAuthorityDefaults} bean to customize the default role prefix in Spring Security.
     * This configuration removes the default "ROLE_" prefix.
     *
     * @return A {@link GrantedAuthorityDefaults} instance with no role prefix.
     * @since 0.1.0
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    /**
     * Creates a {@link JwtEncoder} bean for encoding JWT tokens with the configured secret key.
     *
     * @return A {@link JwtEncoder} instance for token encoding.
     * @since 0.1.0
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.jwtKey.getBytes()));
    }

    /**
     * Creates a {@link JwtDecoder} bean for decoding JWT tokens using the configured secret key.
     *
     * @return A {@link JwtDecoder} instance for token decoding.
     * @since 0.1.0
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtKey.getBytes(), 0, this.jwtKey.getBytes().length, "HS256");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        jwtDecoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO)); // no clock skew
        return jwtDecoder;
    }

    /**
     * Configures the {@link AuthenticationManager} to use the custom user details service and password encoder.
     * This method demonstrates how to configure the authentication mechanism in Spring Security.
     *
     * @param http The {@link HttpSecurity} to configure
     * @param passwordEncoder The {@link PasswordEncoder} to use for encoding passwords
     * @return An {@link AuthenticationManager} instance after configuration.
     * @throws Exception if an error occurs during configuration
     * @since 0.1.0
     */
    @Bean
    public AuthenticationManager
    authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    /**
     * Defines a {@link PasswordEncoder} bean that uses the BCrypt hashing algorithm. This encoder is used for hashing and verifying passwords.
     *
     * @return A {@link BCryptPasswordEncoder} instance.
     * @since 0.1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
