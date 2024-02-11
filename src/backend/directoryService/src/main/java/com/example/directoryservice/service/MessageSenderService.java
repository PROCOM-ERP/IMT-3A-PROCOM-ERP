package com.example.directoryservice.service;

import com.example.directoryservice.model.Path;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderService implements CommandLineRunner {

    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange rolesDirectExchange;

    private final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);

    @Override
    public void run(String... args) {
        logger.info("Sending message about service initialisation to retrieve its roles...");
        String resource = Path.ROLES;
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesDirectExchange.getName(), "roles.init", path);
        logger.info("Message sent");
    }

    public void sendRoleActivationMessage(String roleName) {
        logger.info("Sending message to set a role activation status...");
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesDirectExchange.getName(), "role.activation", path);
        logger.info("Message sent");
    }
}
