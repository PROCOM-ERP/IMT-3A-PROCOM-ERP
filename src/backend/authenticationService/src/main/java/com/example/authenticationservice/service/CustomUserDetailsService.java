package com.example.authenticationservice.service;

import com.example.authenticationservice.model.Employee;
import com.example.authenticationservice.repository.EmployeeRepository;
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

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(username);
        return employee.map(
                e -> new User(username, e.getPassword(), Collections.emptyList()))
                .orElseGet(() -> new User(username, "", Collections.emptyList()));
    }
}
