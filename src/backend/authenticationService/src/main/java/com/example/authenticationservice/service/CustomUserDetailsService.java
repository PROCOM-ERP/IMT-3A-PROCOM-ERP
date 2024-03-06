package com.example.authenticationservice.service;

import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.repository.LoginProfileRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LoginProfile> loginProfile = loginProfileRepository.findById(username);
        return loginProfile.map(
                e -> new User(username, e.getPassword(), Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }
}
