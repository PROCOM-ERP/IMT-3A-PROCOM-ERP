package com.example.authenticationservice.service;

import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {

    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final RabbitTemplate rabbitTemplate;
    @Qualifier("rolesFanoutExchange")
    private final FanoutExchange rolesFanoutExchange;
    @Qualifier("loginProfilesSecExchange")
    private final FanoutExchange loginProfilesSecExchange;

    private final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);

    @Autowired
    public MessageSenderService(CustomHttpRequestBuilder customHttpRequestBuilder, RabbitTemplate rabbitTemplate, FanoutExchange rolesFanoutExchange, FanoutExchange loginProfilesSecExchange) {
        this.customHttpRequestBuilder = customHttpRequestBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.rolesFanoutExchange = rolesFanoutExchange;
        this.loginProfilesSecExchange = loginProfilesSecExchange;
    }

    public void sendRolesNewMessage(String roleName) {
        logger.info("Sending message to inform the network about role creation...");
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesFanoutExchange.getName(), "role.activation", path);
        logger.info("Message sent");
    }

    public void sendLoginProfilesJwtDisableOldMessage() {
        logger.info("Sending message to expire all login profile Jwt...");
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profiles.jwt.disable.old", "");
        logger.info("Message sent");
    }

    public void sendLoginProfileJwtDisableOldMessage(String idLoginProfile) {
        logger.info("Sending message to expire a login profile Jwt...");
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.jwt.disable.old", idLoginProfile);
        logger.info("Message sent");
    }

    public void sendLoginProfileEnableModify(String idLoginProfile) {
        logger.info("Sending message to set a login profile activation status...");
        String resource = String.format("%s/%s%s", Path.LOGIN_PROFILES, idLoginProfile, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.activation", path);
        logger.info("Message sent");
    }
}
