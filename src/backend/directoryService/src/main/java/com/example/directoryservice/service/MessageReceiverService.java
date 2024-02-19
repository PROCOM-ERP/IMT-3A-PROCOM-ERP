package com.example.directoryservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final RoleService roleService;
    private final LoginProfileService loginProfileService;

    private final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

    /* Public Methods */

    @RabbitListener(queues = "roles-new-queue")
    public void receiveRolesNewMessage(String getRoleByNamePath)
    {
        logger.info("Message received to create a role: " + getRoleByNamePath);
        try {
            // try to save all microservice roles
            roleService.createRole(getRoleByNamePath);
            logger.info("Role successfully created / ignored");
        } catch (RestClientException ignored) {
            logger.error("Role creation failed");
        }
    }

    @RabbitListener(queues = "login-profiles-sec-queue")
    public void receiveLoginProfilesSecMessage(String message, @Header("amqp_receivedRoutingKey") String routingKey)
    {
        switch (routingKey) {
            case "login-profiles.new":
                receiveLoginProfilesNewMessage(message);
                break;
            case "login-profile.activation":
                receiveLoginProfileActivationMessage(message);
                break;
            case "login-profiles.jwt.disable.old":
                receiveLoginProfilesJwtDisableOldMessage();
                break;
            case "login-profile.jwt.disable.old":
                receiveLoginProfileJwtDisableOldMessage(message);
                break;
        }
    }

    /* Private Methods */
    private void receiveLoginProfilesNewMessage(String idLoginProfile)
    {
        logger.info("Message received to create a login profile: " + idLoginProfile);
        loginProfileService.createLoginProfile(idLoginProfile);
        logger.info("Login profile successfully created");
    }

    private void receiveLoginProfileActivationMessage(String getLoginProfileActivationById)
    {
        logger.info("Message received to update a login profile activation: " + getLoginProfileActivationById);
        try {
            loginProfileService.updateLoginProfileActivationById(getLoginProfileActivationById);
            logger.info("Login profile activation successfully updated");
        } catch (RestClientException e) {
            logger.error("Login profile activation update failed", e);
        }
    }

    private void receiveLoginProfilesJwtDisableOldMessage()
    {
        logger.info("Message received to update all login profiles jwt min generation instant");
        loginProfileService.updateAllLoginProfileJwtGenMin();
        logger.info("All login profiles jwt min generation instant successfully updated");
    }

    private void receiveLoginProfileJwtDisableOldMessage(String idLoginProfile)
    {
        logger.info("Message received to update a login profile jwt min generation instant: " + idLoginProfile);
        loginProfileService.updateLoginProfileJwtGenMinAtById(idLoginProfile);
        logger.info("Login profile jwt min generation instant successfully updated");
    }
}
