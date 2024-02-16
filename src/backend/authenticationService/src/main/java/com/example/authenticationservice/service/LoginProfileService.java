package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.CustomPasswordGenerator;
import com.example.authenticationservice.utils.PerformanceTracker;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginProfileService {

    private final LoginProfileRepository loginProfileRepository;
    private final RoleRepository roleRepository;
    private final CustomPasswordGenerator customPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final MessageSenderService messageSenderService;

    private final PerformanceTracker performanceTracker;
    private final Logger logger = LoggerFactory.getLogger(LoginProfileService.class);

    /* Public Methods */

    @Transactional
    public String createLoginProfile(LoginProfileCreationRequestDto loginProfileCreationRequestDto) throws Exception {
        logger.info("Start login profile creation...");
        long startTimeNano = performanceTracker.getCurrentTime();

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

        // send mail to the new user
        mailService.sendNewLoginProfileMail(idLoginProfile, password);

        // send message to inform the network about login profile creation
        messageSenderService.sendLoginProfilesNewMessage(idLoginProfile);

        // return generated idLoginProfile
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to create new login profile : " + elapsedTimeMillis + " ms");
        return idLoginProfile;
    }

    public LoginProfileResponseDto getLoginProfileById(String idLoginProfile)
            throws NoSuchElementException {

        // check if LoginProfile entity exists and retrieve it
        LoginProfile loginProfile = loginProfileRepository.findById(idLoginProfile).orElseThrow();

        // get RoleDto entities from the login profile existing ones
        Set<RoleDto> roles = roleRepository.findAll().stream()
                .map(r -> RoleDto.builder()
                        .name(r.getName())
                        .isEnable(loginProfile.getRoles().contains(r))
                        .build())
                .collect(Collectors.toSet());

        // build LoginProfileDto entity
        LoginProfileResponseDto loginProfileDto = LoginProfileResponseDto.builder()
                .isEnable(loginProfile.getIsEnable())
                .roles(roles)
                .build();

        // return LoginProfileResponseDto entity
        return loginProfileDto;
    }

    public LoginProfileActivationResponseDto getLoginProfileActivationById(String idLoginProfile)
            throws NoSuchElementException {
        return loginProfileRepository.findById(idLoginProfile)
                .map(loginProfile -> LoginProfileActivationResponseDto.builder()
                        .id(loginProfile.getId())
                        .isEnable(loginProfile.getIsEnable())
                        .build())
                .orElseThrow();
    }

    public void updateLoginProfilePasswordById(String idLoginProfile, String password)
            throws AccessDeniedException, NoSuchElementException {

        // check if the loginProfile to be modified is the same as the authenticated one
        String currentLoginProfileId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentLoginProfileId.equals(idLoginProfile)) {
            throw new AccessDeniedException("");
        }

        // check password validity
        customPasswordGenerator.checkPasswordValidity(password);

        // try to update the password
        int row = loginProfileRepository.updatePasswordById(idLoginProfile, passwordEncoder.encode(password));

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

        // send message to inform the network about a login profile jwt expiration
        messageSenderService.sendLoginProfileJwtDisableOldMessage(idLoginProfile);

    }

    @Transactional
    public void updateLoginProfileById(String idLoginProfile,
                                       LoginProfileUpdateRequestDto loginProfileDto)
    throws NoSuchElementException, DataIntegrityViolationException
    {

        // check if loginProfile exists
        LoginProfile loginProfile = loginProfileRepository.findById(idLoginProfile).orElseThrow();

        // update LoginProfile entity roles
        if (loginProfileDto.getRoles() != null) {
            loginProfile.setRoles(loginProfileDto.getRoles().stream()
                    .map(roleName -> Role.builder()
                            .name(roleName)
                            .build())
                    .collect(Collectors.toSet()));
        }


        // update LoginProfile entity activation status
        if (loginProfileDto.getIsEnable() != null)
            loginProfile.setIsEnable(loginProfileDto.getIsEnable());

        // save modifications
        loginProfileRepository.save(loginProfile);

        // send message to other services to update jwt_min_creation for current loginProfile
        if (loginProfileDto.getIsEnable() != null) {
            messageSenderService.sendLoginProfileActivation(idLoginProfile);
        } else if (loginProfileDto.getRoles() != null) {
            int row = loginProfileRepository.updateJwtGenMinAtById(idLoginProfile);
            // check if only 1 row was modified
            if (row != 1) {
                throw new NoSuchElementException();
            }
            messageSenderService.sendLoginProfileJwtDisableOldMessage(idLoginProfile);
        }
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

    /* Private Methods */

    private String generateIdLoginProfileFromNextId(Integer nextIdLoginProfile) {
        int letterAsciiIndex = 65 + (nextIdLoginProfile / 100000) % 26;
        int numericPart = nextIdLoginProfile % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }
}
