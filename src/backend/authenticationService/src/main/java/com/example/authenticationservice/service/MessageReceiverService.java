package com.example.authenticationservice.service;

import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final RoleService roleService;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    private final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

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

    @RabbitListener(queues = "role-activation-queue")
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



}
