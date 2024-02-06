package com.example.authenticationservice.utils;

import com.example.authenticationservice.model.Path;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    public void sendLoginProfilesJwtDisableOldMessage() {
        logger.info("Sending message to expire all login-profile Jwt...");
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "login-profiles.jwt.disable.old", "");
        logger.info("Message sent");
    }

    public void sendLoginProfileJwtDisableOldMessage(String idLoginProfile) {
        logger.info("Sending message to expire a login-profile Jwt...");
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "login-profile.jwt.disable.old", idLoginProfile);
        logger.info("Message sent");
    }

    public void sendLoginProfileEnableModify(String idLoginProfile) {
        logger.info("Sending message to set a login-profile activation status...");
        String resource = String.format("%s/%s%s", Path.LOGIN_PROFILES, idLoginProfile, Path.ENABLE);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "login-profile.enable.modify", path);
        logger.info("Message sent");
    }

}
