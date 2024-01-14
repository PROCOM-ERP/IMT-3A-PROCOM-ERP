package com.example.directoryservice.utils;

import com.example.directoryservice.model.Path;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQSender implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange directExchange;
    private final TopicExchange topicExchange;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    @Override
    public void run(String... args) {
        logger.info("Sending message to auth service about roles...");
        String resource = Path.ROLES;
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(directExchange.getName(), "roles.init", path);
        logger.info("Message sent");
    }

    public void sendRoleEnableModifyMessage(String role) {
        logger.info(
                "Sending message to auth service to set a role activation status...");
        String resource = String.format("%s/%s", Path.ROLES, role);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(directExchange.getName(),
                "role.enable.modify", path);
        logger.info("Message sent");
    }

    public void sendRolesNewMessage(String role) {
        logger.info("Sending message to auth service about a new role created...");
        rabbitTemplate.convertAndSend(directExchange.getName(), "roles.new", role);
        logger.info("Message sent");
    }

    public void sendEmployeeEmailModify(String idEmployee) {
        logger.info("Sending message to inform about update on an employee email...");
        String resource = String.format("%s/%s", Path.EMPLOYEES, idEmployee);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(topicExchange.getName(), "employee.email.modify", path);
        logger.info("Message sent");
    }
}
