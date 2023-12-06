package com.example.authservice.config;

import com.example.authservice.service.CustomUserDetailsService;
import com.example.authservice.utils.CustomJwtAuthenticationConverter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String roleAdmin = "admin";
    private final String roleUser = "user";

    @Value("${security.jwt.secretkey}")
    private String jwtKey;

    @Value("${security.jwt.rolekey}")
    private String roleKey;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    @Order(1)
    public SecurityFilterChain basicfilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("api/v1/login/**")
                // specify CORS and CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // remove session and cookies in cache
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                // permissions
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                // authentication methods
                .httpBasic(Customizer.withDefaults())
                // finalize the build
                .build();
    }

    @Bean
    public SecurityFilterChain jwtfilterChain(HttpSecurity http) throws Exception {
        return http
                // specify CORS and CSRF
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // remove session and cookies in cache
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
                // permissions
                .authorizeHttpRequests((auth) -> {
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/hello").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/docs/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAnyAuthority(roleAdmin);
                    auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyAuthority(roleAdmin, roleUser);
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyAuthority(roleAdmin, roleUser);
                    auth.anyRequest().authenticated();
                })
                // authentication methods
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter(roleKey))))
                // finalize the build
                .build();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.jwtKey.getBytes()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtKey.getBytes(), 0, this.jwtKey.getBytes().length,"RSA");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        jwtDecoder.setJwtValidator(new JwtTimestampValidator(Duration.ZERO)); // no clock skew

        return jwtDecoder;
    }
}
