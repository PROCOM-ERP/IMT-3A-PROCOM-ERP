package com.example.authenticationservice.service;

import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginProfileRepository loginProfileRepository;
    private final CustomLogger logger;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LoginProfile> loginProfile = loginProfileRepository.findById(username);
        if (loginProfile.isEmpty())
            logger.error(username + " doesn't exist.", CustomLogger.TAG_JWT, "loadUserByUsername", HttpStatus.FORBIDDEN);
        return loginProfile.map(
                e -> new User(username, e.getPassword(), Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }
}
