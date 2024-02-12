package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.LoginProfileActivationResponseDto;
import com.example.authenticationservice.dto.LoginProfileCreationRequestDto;
import com.example.authenticationservice.dto.LoginProfileResponseDto;
import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.utils.CustomPasswordGenerator;
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

    private final LoginProfileRepository loginProfileRepository;
    private final CustomPasswordGenerator customPasswordGenerator;
    private final PasswordEncoder passwordEncoder;

    // private final Logger logger = LoggerFactory.getLogger(LoginProfileService.class);

    /* Public Methods */

    @Transactional
    public String createLoginProfile(LoginProfileCreationRequestDto loginProfileCreationRequestDto) {

        // generate random password
        String password = customPasswordGenerator.generateRandomPassword();

        // create loginProfile
        Integer nextIdLoginProfile = loginProfileRepository.getNextIdLoginProfile();
        String idLoginProfile = generateIdLoginProfileFromNextId(nextIdLoginProfile);
        LoginProfile loginProfile = LoginProfile.builder()
                .id(idLoginProfile)
                .idLoginProfileGen(nextIdLoginProfile)
                .email(loginProfileCreationRequestDto.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(loginProfileCreationRequestDto.getRoles().stream()
                        .map(roleName -> Role.builder()
                                .name(roleName)
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        // try to save loginProfile with its roles and return its id
        loginProfileRepository.save(loginProfile);

        return idLoginProfile;
    }

    public List<LoginProfileResponseDto> getAllLoginProfiles() {
        return loginProfileRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .toList();
    }

    public LoginProfileResponseDto getLoginProfile(String idLoginProfile)
            throws NoSuchElementException {
        return loginProfileRepository.findById(idLoginProfile)
                .map(this::modelToResponseDto)
                .orElseThrow();
    }

    public LoginProfileActivationResponseDto getLoginProfileActivation(String idLoginProfile)
            throws NoSuchElementException {
        return loginProfileRepository.findById(idLoginProfile)
                .map(loginProfile -> LoginProfileActivationResponseDto.builder()
                        .id(loginProfile.getId())
                        .isEnable(loginProfile.getIsEnable())
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

    public void updateLoginProfileActivation(String idLoginProfile, Boolean isEnable)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update activation status
        int row = loginProfileRepository.updateActivationById(idLoginProfile, isEnable);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

        // send message to other services to update jwt_min_creation for current loginProfile
    }

    /* Private Methods */

    private String generateIdLoginProfileFromNextId(Integer nextIdLoginProfile) {
        int letterAsciiIndex = 65 + (nextIdLoginProfile / 100000) % 26;
        int numericPart = nextIdLoginProfile % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }

    private void checkPasswordValidity(String password)
            throws IllegalArgumentException {
        if (!password.matches(customPasswordGenerator.getRegexPassword()))
            throw new IllegalArgumentException();
    }

    private LoginProfileResponseDto modelToResponseDto(LoginProfile loginProfile) {
        return LoginProfileResponseDto.builder()
                .id(loginProfile.getId())
                .isEnable(loginProfile.getIsEnable())
                .roles(loginProfile.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
