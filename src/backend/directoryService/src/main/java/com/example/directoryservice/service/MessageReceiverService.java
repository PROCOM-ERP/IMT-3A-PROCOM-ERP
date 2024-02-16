package com.example.directoryservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final RoleService roleService;
    private final LoginProfileService loginProfileService;

    private final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

    @RabbitListener(queues = "roles-new-queue")
    public void receiveRolesNewMessage(String getRoleByNamePath) {
        logger.info("Message received to create a role: " + getRoleByNamePath);
        try {
            // try to save all microservice roles
            roleService.createRole(getRoleByNamePath);
            logger.info("Role successfully created / ignored");
        } catch (RestClientException ignored) {
            logger.error("Role creation failed");
        }
    }
}
