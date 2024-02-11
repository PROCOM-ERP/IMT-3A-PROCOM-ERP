package com.example.directoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class MessageReceiverService {

    private final RoleService roleService;

    private final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

    @Autowired
    public MessageReceiverService(RoleService roleService) {
        this.roleService = roleService;
    }

    @RabbitListener(queues = "roles.new")
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
