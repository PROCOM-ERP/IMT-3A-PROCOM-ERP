package com.example.authservice.service;

import com.example.authservice.dto.UserRequestDto;
import com.example.authservice.dto.UserResponseDto;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<?> createUserFromRequestDTO(UserRequestDto userRequestDto) {
        // create user
        Integer indexUser = userRepository.getNextValIndexUser();
        User user = User.builder()
                .idUser(generateIdUserFromIndex(indexUser))
                .indexUser(indexUser)
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .role(Role.builder()
                        .role(userRequestDto.getRole())
                        .build())
                .timestamp(LocalDate.now())
                .build();

        // try to save user and return the DTO version
        try {
            User savedUser = userRepository.save(user);
            return Optional.of(userToResponseDto(savedUser));
        } catch (DataAccessException | IllegalArgumentException e) {
            return Optional.of(e);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::userToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> getUserById(String idUser) {
        return userRepository.findById(idUser)
                .map(this::userToResponseDto);
    }

    public Optional<UserResponseDto> updateUserById(String idUser, UserRequestDto userRequestDto) {

        // check if user exists
        Optional<User> user = userRepository.findById(idUser);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        // check whether this is an attempt to change the password
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        int row = 0;
        if (userRequestDto.getPassword() != null && currentUserId.equals(idUser)) {
            String password = passwordEncoder.encode(userRequestDto.getPassword());
            row = userRepository.updateUserPasswordByIdUser(idUser, password);
        }

        // update user
        return row > 0 ? Optional.of(userToResponseDto(user.get())) : Optional.empty();
    }

    private String generateIdUserFromIndex(Integer indexUser) {
        int letterAsciiIndex = 65 + (indexUser / 100000) % 26;
        int numericPart = indexUser % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }

    private UserResponseDto userToResponseDto(User user) {
        return UserResponseDto.builder()
                .idUser(user.getIdUser())
                .build();
    }
}
