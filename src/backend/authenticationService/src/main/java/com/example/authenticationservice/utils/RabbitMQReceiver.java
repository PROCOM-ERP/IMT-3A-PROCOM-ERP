package com.example.authenticationservice.utils;

import com.example.authenticationservice.dto.LoginProfileMessageDto;
import com.example.authenticationservice.service.LoginProfileService;
import com.example.authenticationservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class RabbitMQReceiver {

    private final RoleService roleService;
    private final LoginProfileService loginProfileService;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitListener(queues = "role-enable-modify-queue")
    public void receiveRoleEnableModifyMessage(String getRoleByNamePath) {
        logger.info("Message received to set a role activation status: " + getRoleByNamePath);
        /*
        try {
            // try to get external role
            RoleActivationDto roleActivation = roleService.getExternalRole(getRoleByNamePath);
            // update local roles
            roleService.saveExternalRole(roleActivation);
            logger.info("Role activation status successfully set");
        } catch (NoSuchElementException | RestClientException ignored) {
            logger.error("Role activation status set failed");
        }

         */
    }

    @RabbitListener(queues = "roles-init-queue")
    public void receiveRolesInitMessage(String getAllRolesPath) {
        logger.info("Message received on startup of a service to init its roles: " + getAllRolesPath);
        /*
        try {
            // try to get external role
            List<RoleActivationDto> roleActivations = roleService.getAllExternalRoles(getAllRolesPath);
            // update local roles
            roleService.saveAllExternalRoles(roleActivations);
            logger.info("Roles successfully initialised");
        } catch (NoSuchElementException | RestClientException ignored) {
            logger.error("Roles initialisation failed");
        }

         */
    }

    @RabbitListener(queues = "roles-new-queue")
    public void receiveRolesNewMessage(String getRoleByNamePath) {
        logger.info("Message received to add another service new role: " + getRoleByNamePath);
        /*
        try {
            // try to get external role
            RoleActivationDto roleActivation = roleService.getExternalRole(getRoleByNamePath);
            // update local roles
            roleService.saveExternalRole(roleActivation);
            logger.info("Role successfully saved");
        } catch (NoSuchElementException | RestClientException ignored) {
            logger.error("Role save failed");
        }

         */
    }

    @RabbitListener(queues = "login-profile-email-queue")
    public void receiveLoginProfileEmailMessage(String getLoginProfileByIdPath) {
        logger.info("Message received to update a login-profile email: " + getLoginProfileByIdPath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getLoginProfileByIdPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<LoginProfileMessageDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type
        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
            logger.info("Login-profile email successfully updated");
            LoginProfileMessageDto loginProfile = response.getBody();
            loginProfileService.updateLoginProfileEmail(loginProfile.getId(), loginProfile.getEmail());
        } else {
            logger.error("Login-profile email update failed");
        }
    }
}
