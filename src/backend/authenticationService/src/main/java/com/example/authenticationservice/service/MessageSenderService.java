package com.example.authenticationservice.service;

import com.example.authenticationservice.annotation.LogMessageSent;
import com.example.authenticationservice.model.Path;
import com.example.authenticationservice.utils.CustomHttpRequestBuilder;
import com.example.authenticationservice.utils.CustomLogger;
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

    @Autowired
    public MessageSenderService(CustomHttpRequestBuilder customHttpRequestBuilder, RabbitTemplate rabbitTemplate,
            FanoutExchange rolesFanoutExchange, FanoutExchange loginProfilesSecExchange) {
        this.customHttpRequestBuilder = customHttpRequestBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.rolesFanoutExchange = rolesFanoutExchange;
        this.loginProfilesSecExchange = loginProfilesSecExchange;
    }

    @LogMessageSent(tag = CustomLogger.TAG_ROLES,
            routingPattern = "roles.new",
            deliveryMethod = "Broadcast",
            description = "Message sent to inform the network about role creation.")
    public void sendRolesNewMessage(String roleName) {
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesFanoutExchange.getName(), "roles.new", path);
    }

    @LogMessageSent(tag = CustomLogger.TAG_USERS,
            routingPattern = "login-profiles.new",
            deliveryMethod = "Broadcast",
            description = "Message sent to inform the network about user login profile creation.")
    public void sendLoginProfilesNewMessage(String idLoginProfile) {
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profiles.new", idLoginProfile);
    }

    @LogMessageSent(tag = CustomLogger.TAG_USERS,
            routingPattern = "login-profiles.jwt.disable.old",
            deliveryMethod = "Broadcast",
            description = "Message sent to inform the network that all users' tokens must expire.")
    public void sendLoginProfilesJwtDisableOldMessage() {
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profiles.jwt.disable.old", "");
    }

    @LogMessageSent(tag = CustomLogger.TAG_USERS,
            routingPattern = "login-profile.jwt.disable.old",
            deliveryMethod = "Broadcast",
            description = "Message sent to inform the network that a user token must expire.")
    public void sendLoginProfileJwtDisableOldMessage(String idLoginProfile) {
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.jwt.disable.old",
                idLoginProfile);
    }

    @LogMessageSent(tag = CustomLogger.TAG_MESSAGE,
            routingPattern = "dead.letter",
            deliveryMethod = "Unicast",
            description = "Message sent to the dead letter exchange.")
    public void sendToDeadLetterQueueFrom(Message originalMessage, String originalQueue) {
        Message dlqMessage = MessageBuilder.withBody(originalMessage.getBody())
                .setContentType(originalMessage.getMessageProperties().getContentType())
                .setHeader("x-original-queue", originalQueue)
                .setHeader("x-retry-count", originalMessage.getMessageProperties().getHeader("x-retry-count"))
                .build();

        // Send to DLQ (assuming you have a DLQ setup with routing and exchanges)
        rabbitTemplate.send("dead-letter-exchange", "dead.letter", dlqMessage);
    }

    @LogMessageSent(tag = CustomLogger.TAG_USERS,
            routingPattern = "login-profile.activation",
            deliveryMethod = "Broadcast",
            description = "Message sent to inform the network that a user activation status must be set.")
    public void sendLoginProfileActivation(String idLoginProfile) {
        String resource = String.format("%s/%s%s", Path.LOGIN_PROFILES, idLoginProfile, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(loginProfilesSecExchange.getName(), "login-profile.activation", path);
    }

    @LogMessageSent(tag = CustomLogger.TAG_ROLES,
            routingPattern = "roles.init",
            deliveryMethod = "Unicast",
            description = "Message sent to inform the authentication service that a service roles have to be retrieved.")
    public void resendMessageWithDelay(Message message, String originalQueue, long delay) {
        // Resend the message after the delay
        try {
            Thread.sleep(delay);
            rabbitTemplate.convertAndSend(originalQueue, message);
        } catch (Exception ignored) {
        }
    }
}
