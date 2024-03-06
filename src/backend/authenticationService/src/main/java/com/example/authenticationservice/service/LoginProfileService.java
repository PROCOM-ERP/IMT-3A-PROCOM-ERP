package com.example.authenticationservice.service;

import com.example.authenticationservice.annotation.LogExecutionTime;
import com.example.authenticationservice.dto.*;
import com.example.authenticationservice.model.LoginProfile;
import com.example.authenticationservice.model.Permission;
import com.example.authenticationservice.model.Role;
import com.example.authenticationservice.repository.LoginProfileRepository;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginProfileService {

    /* Constants */
    public static final String ERROR_MSG_USER_ID =
            "User id should start by a capital letter, followed by exactly 5 digits.";

    /* Repository Beans */
    private final LoginProfileRepository loginProfileRepository;
    private final RoleRepository roleRepository;

    /* Service Beans */
    private final MailService mailService;
    private final MessageSenderService messageSenderService;

    /* Utils Beans */
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final CustomPasswordGenerator customPasswordGenerator;
    private final CustomStringUtils customStringUtils;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    /* Public Methods */

    @Transactional
    @LogExecutionTime(description = "Create new user login profile.",
            tag = CustomLogger.TAG_USERS)
    public LoginProfileIdResponseDto createLoginProfile(
            LoginProfileCreationRequestDto loginProfileCreationRequestDto)
            throws Exception
    {
        // sanitize all request parameters
        customStringUtils.sanitizeAllStrings(loginProfileCreationRequestDto);

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
        return LoginProfileIdResponseDto.builder().id(idLoginProfile).build();
    }

    @LogExecutionTime(description = "Retrieve a user profile roles and activation status.",
            tag = CustomLogger.TAG_USERS)
    public LoginProfileResponseDto getLoginProfileById(String idLoginProfile)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check if LoginProfile id respect the good pattern
        customStringUtils.checkStringPattern(idLoginProfile, CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
                ERROR_MSG_USER_ID);

        // check if LoginProfile entity exists and retrieve it
        LoginProfile loginProfile = loginProfileRepository.findById(idLoginProfile)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing user with id " + idLoginProfile + "."));

        // get RoleDto entities from the login profile existing ones
        Set<RoleDto> roles = roleRepository.findAll().stream()
                .map(r -> RoleDto.builder()
                        .name(r.getName())
                        .isEnable(loginProfile.getRoles().contains(r))
                        .build())
                .collect(Collectors.toSet());

        // build and return LoginProfileResponseDto entity
        return LoginProfileResponseDto.builder()
                .isEnable(loginProfile.getIsEnable())
                .roles(roles)
                .build();
    }

    @LogExecutionTime(description = "Retrieve a user profile activation status.",
            tag = CustomLogger.TAG_USERS)
    public LoginProfileActivationResponseDto getLoginProfileActivationById(String idLoginProfile)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check if LoginProfile id respect the good pattern
        customStringUtils.checkStringPattern(idLoginProfile, CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
                ERROR_MSG_USER_ID);

        return loginProfileRepository.findById(idLoginProfile)
                .map(loginProfile -> LoginProfileActivationResponseDto.builder()
                        .id(loginProfile.getId())
                        .isEnable(loginProfile.getIsEnable())
                        .build())
                .orElseThrow(() ->
                        new NoSuchElementException("No existing user with id " + idLoginProfile + "."));
    }

    @LogExecutionTime(description = "Update a user profile password.",
            tag = CustomLogger.TAG_USERS)
    public void updateLoginProfilePasswordById(
            String idLoginProfile,
            LoginProfilePasswordUpdateRequestDto passwordDto)
            throws IllegalArgumentException,
            AccessDeniedException,
            NoSuchElementException
    {
        // check if LoginProfile id respect the good pattern
        customStringUtils.checkStringPattern(idLoginProfile, CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
                ERROR_MSG_USER_ID);

        // sanitize all request parameters
        customStringUtils.sanitizeAllStrings(passwordDto);

        // check if the LoginProfile, for which the password will be modified,
        // is the same as the authenticated one (or admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginProfileId = authentication.getName();
        boolean canBypassAccessDeny = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(Permission.CanBypassAccessDeny.name()));
        if (!currentLoginProfileId.equals(idLoginProfile) && !canBypassAccessDeny) {
            throw new AccessDeniedException("");
        }

        // check password validity
        String password = passwordDto.getPassword();

        // try to update the password
        int row = loginProfileRepository.updatePasswordById(idLoginProfile, passwordEncoder.encode(password));

        // check if only 1 row was modified
        if (row != 1)
            throw new NoSuchElementException("No existing user with id " + idLoginProfile + ".");

        // send message to inform the network about a login profile jwt expiration
        messageSenderService.sendLoginProfileJwtDisableOldMessage(idLoginProfile);

    }

    @Transactional
    @LogExecutionTime(description = "Update a user profile roles and / or activation status.",
            tag = CustomLogger.TAG_USERS)
    public void updateLoginProfileById(
            String idLoginProfile,
            LoginProfileUpdateRequestDto loginProfileDto)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check if LoginProfile id respect the good pattern
        customStringUtils.checkStringPattern(idLoginProfile, CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
                ERROR_MSG_USER_ID);

        // sanitize all request parameters
        customStringUtils.sanitizeAllStrings(loginProfileDto);

        // check if loginProfile exists
        LoginProfile loginProfile = loginProfileRepository.findById(idLoginProfile)
                .orElseThrow(() ->
                        new NoSuchElementException("No existing user with id " + idLoginProfile + "."));

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
        if (loginProfileDto.getIsEnable() != null)
            messageSenderService.sendLoginProfileActivation(idLoginProfile);
        if (loginProfileDto.getRoles() != null) {
            int row = loginProfileRepository.updateJwtGenMinAtById(idLoginProfile);
            // check if only 1 row was modified
            if (row != 1)
                throw new NoSuchElementException("No existing user with id " + idLoginProfile + ".");
            messageSenderService.sendLoginProfileJwtDisableOldMessage(idLoginProfile);
        }
    }

    @LogExecutionTime(description = "Update a user profile email address.",
            tag = CustomLogger.TAG_USERS)
    public void updateLoginProfileEmail(String getEmployeeEmailById)
            throws RestClientException {

        // retrieve external EmployeeEmailResponseDto entity
        EmployeeEmailResponseDto employeeDto = getEmployeeEmailById(getEmployeeEmailById);

        // try to update the email
        int row =  loginProfileRepository.updateEmailById(employeeDto.getId(), employeeDto.getEmail());

        // check if only 1 row was modified
        if (row != 1) {
            throw new RestClientException("");
        }
    }

    /* Private Methods */

    private String generateIdLoginProfileFromNextId(Integer nextIdLoginProfile) {
        int letterAsciiIndex = 65 + (nextIdLoginProfile / 100000) % 26;
        int numericPart = nextIdLoginProfile % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }

    private EmployeeEmailResponseDto getEmployeeEmailById(String getEmployeeEmailById)
        throws RestClientException
    {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getEmployeeEmailById);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<EmployeeEmailResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new RestClientException("");

        // return expected Employee email
        return response.getBody();
    }
}
