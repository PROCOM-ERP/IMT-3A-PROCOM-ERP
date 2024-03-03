package com.example.authenticationservice.service;

import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
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
    public MessageSenderService(CustomHttpRequestBuilder customHttpRequestBuilder, RabbitTemplate rabbitTemplate,
            FanoutExchange rolesFanoutExchange, FanoutExchange loginProfilesSecExchange) {
        this.customHttpRequestBuilder = customHttpRequestBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.rolesFanoutExchange = rolesFanoutExchange;
        this.loginProfilesSecExchange = loginProfilesSecExchange;
    }

    public void sendRolesNewMessage(String roleName) {
        logger.info("Sending message to inform the network about role creation...");
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesFanoutExchange.getName(), "roles.new", path);
        logger.info("Message sent");
    }

    public void sendLoginProfilesNewMessage(String idLoginProfile) {
        logger.info("Sending message to inform the network about login profile creation...");
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profiles.new", idLoginProfile);
        logger.info("Message sent");
    }

    public void sendLoginProfilesJwtDisableOldMessage() {
        logger.info("Sending message to expire all login profile Jwt...");
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profiles.jwt.disable.old", "");
        logger.info("Message sent");
    }

    public void sendLoginProfileJwtDisableOldMessage(String idLoginProfile) {
        logger.info("Sending message to expire a login profile Jwt...");
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.jwt.disable.old",
                idLoginProfile);
        logger.info("Message sent");
    }

    public void sendToDeadLetterQueueFrom(Message originalMessage, String originalQueue) {
        Message dlqMessage = MessageBuilder.withBody(originalMessage.getBody())
                .setContentType(originalMessage.getMessageProperties().getContentType())
                .setHeader("x-original-queue", originalQueue)
                .setHeader("x-retry-count", originalMessage.getMessageProperties().getHeader("x-retry-count"))
                .build();

        // Send to DLQ (assuming you have a DLQ setup with routing and exchanges)
        rabbitTemplate.send("dead-letter-exchange", "dead.letter", dlqMessage);
    }

    public void sendLoginProfileActivation(String idLoginProfile) {
        logger.info("Sending message to set a login profile activation status...");
        String resource = String.format("%s/%s%s", Path.LOGIN_PROFILES, idLoginProfile, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.activation", path);
        logger.info("Message sent");
    }

    public void resendMessageWithDelay(Message message, String originalQueue, long delay) {
        // Resend the message after the delay
        try {
            Thread.sleep(delay);
            rabbitTemplate.convertAndSend(originalQueue, message);
        } catch (Exception e) {
            logger.error("Failed to sleep exponential delay time", e);
        }
    }
}
