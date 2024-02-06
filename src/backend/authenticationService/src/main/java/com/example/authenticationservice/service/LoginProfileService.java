package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.LoginProfileIsEnableMessageDto;
import com.example.authenticationservice.dto.LoginProfileRequestDto;
import com.example.authenticationservice.dto.LoginProfileResponseDto;
import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.utils.RabbitMQSender;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginProfileService {

    private static final String regexPassword =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*])(?=\\S+$).{12,}$";

    private final LoginProfileRepository loginProfileRepository;
    private final RabbitMQSender rabbitMQSender;
    private final PasswordEncoder passwordEncoder;

    // private final Logger logger = LoggerFactory.getLogger(LoginProfileService.class);

    @Transactional
    public String createLoginProfile(LoginProfileRequestDto loginProfileRequestDto) {

        // check password validity
        String password = loginProfileRequestDto.getPassword();
        checkPasswordValidity(password);

        // create loginProfile
        Integer nextIdLoginProfile = loginProfileRepository.getNextIdLoginProfile();
        LoginProfile loginProfile = LoginProfile.builder()
                .id(generateIdLoginProfileFromNextId(nextIdLoginProfile))
                .idLoginProfileGen(nextIdLoginProfile)
                .email(loginProfileRequestDto.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(loginProfileRequestDto.getRoles().stream()
                        .map(roleName -> Role.builder()
                                .name(roleName)
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        // try to save loginProfile with its roles and return its id
        return loginProfileRepository.save(loginProfile).getId();
    }

    public List<LoginProfileResponseDto> getAllLoginProfiles() {
        return loginProfileRepository.findAll().stream()
                .map(LoginProfileService::modelToResponseDto)
                .toList();
    }

    public LoginProfileResponseDto getLoginProfile(String idLoginProfile)
            throws NoSuchElementException {
        return loginProfileRepository.findById(idLoginProfile)
                .map(LoginProfileService::modelToResponseDto)
                .orElseThrow();
    }

    public LoginProfileIsEnableMessageDto getLoginProfileEnable(String idLoginProfile)
            throws NoSuchElementException {
        return loginProfileRepository.findById(idLoginProfile)
                .map(loginProfile -> LoginProfileIsEnableMessageDto.builder()
                        .id(loginProfile.getId())
                        .enable(loginProfile.getIsEnable())
                        .build())
                .orElseThrow();
    }

    public void updateLoginProfilePassword(String idLoginProfile, String password)
            throws AccessDeniedException, NoSuchElementException {

        // check if the loginProfile to be modified is the same as the authenticated one
        String currentLoginProfileId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentLoginProfileId.equals(idLoginProfile)) {
            throw new AccessDeniedException("");
        }

        // check password validity
        checkPasswordValidity(password);

        // try to update the password
        int row = loginProfileRepository.updatePasswordById(idLoginProfile, passwordEncoder.encode(password));

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

        // send message to other services to update jwt_min_creation for current loginProfile
        rabbitMQSender.sendLoginProfileJwtDisableOldMessage(idLoginProfile);

    }

    public void updateLoginProfileRoles(String idLoginProfile, List<String> roles)
            throws NoSuchElementException {

        // check if loginProfile exists
        LoginProfile loginProfile = loginProfileRepository.findById(idLoginProfile).orElseThrow();
        // set roles
        loginProfile.setRoles(roles.stream()
                .map(roleName -> Role.builder()
                        .name(roleName)
                        .build())
                .collect(Collectors.toSet()));
        // save modifications
        loginProfileRepository.save(loginProfile);

        // send message to other services to update jwt_min_creation for current loginProfile
        rabbitMQSender.sendLoginProfileJwtDisableOldMessage(idLoginProfile);
    }

    public void updateLoginProfileEmail(String idLoginProfile, String email)
            throws NoSuchElementException {

        // try to update the email
        int row =  loginProfileRepository.updateEmailById(idLoginProfile, email);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateLoginProfileEnable(String idLoginProfile, Boolean enable)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update enable
        int row = loginProfileRepository.updateEnableById(idLoginProfile, enable);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

        // send message to other services to update jwt_min_creation for current loginProfile
        rabbitMQSender.sendLoginProfileEnableModify(idLoginProfile);
    }

    private String generateIdLoginProfileFromNextId(Integer nextIdLoginProfile) {
        int letterAsciiIndex = 65 + (nextIdLoginProfile / 100000) % 26;
        int numericPart = nextIdLoginProfile % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }

    private void checkPasswordValidity(String password)
            throws IllegalArgumentException {
        if (!password.matches(regexPassword))
            throw new IllegalArgumentException();
    }

    static LoginProfileResponseDto modelToResponseDto(LoginProfile loginProfile) {
        return LoginProfileResponseDto.builder()
                .id(loginProfile.getId())
                .email(loginProfile.getEmail())
                .roles(loginProfile.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
