package com.example.authenticationservice.service;

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
    @RabbitListener(queues = "roles-init-queue")
    public void receiveRolesInitMessage(String getAllRolesPath) {
        logger.info("Message received on startup of a service to init its roles: " + getAllRolesPath);
        try {
            // try to save all microservice roles
            roleService.saveAllMicroserviceRoles(getAllRolesPath);
            logger.info("Roles successfully initialised");
        } catch (RestClientException ignored) {
            logger.error("Roles initialisation failed");
        }
    }

    @RabbitListener(queues = "role-activation-queue")
    public void receiveRoleActivationMessage(String getRoleByNamePath) {
        logger.info("Message received to set a role activation status: " + getRoleByNamePath);
        try {
            // try to save microservice role
            roleService.saveMicroserviceRole(getRoleByNamePath);
            logger.info("Role activation status successfully set");
        } catch (RestClientException ignored) {
            logger.error("Role activation status set failed");
        }
    }

    @RabbitListener(queues = "employee-email-queue")
    public void receiveEmployeeEmailMessage(String message, @Header("amqp_receivedRoutingKey") String routingKey) {
        switch (routingKey) {
            case "employee.email.update":
                receiveEmployeeEmailUpdate(message);
                break;
        }
    }

    /* Private Methods */
    private void receiveEmployeeEmailUpdate(String getEmployeeEmailById) {
        logger.info("Message received to update a login-profile email: " + getEmployeeEmailById);
        loginProfileService.updateLoginProfileEmail(getEmployeeEmailById);
        logger.info("Email successfully updated");
    }
}
