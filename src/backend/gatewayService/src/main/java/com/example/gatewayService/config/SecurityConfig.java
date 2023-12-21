// package com.example.gatewayService.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.Customizer;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import
// org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import
// org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.savedrequest.NullRequestCache;
//
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
//
// @Bean
// public SecurityFilterChain basicfilterChain(HttpSecurity http)
// throws Exception {
// return http
// // specify CORS and CSRF
// .cors(AbstractHttpConfigurer::disable)
// .csrf(AbstractHttpConfigurer::disable)
// // remove session and cookies in cache
// .sessionManagement(
// session
// -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
// .requestCache((cache) -> cache.requestCache(new NullRequestCache()))
// // permissions
// .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
// // authentication methods
// .httpBasic(Customizer.withDefaults())
// // finalize the build
// .build();
// }
// }
