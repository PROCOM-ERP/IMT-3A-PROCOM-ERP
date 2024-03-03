package com.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;

import com.example.authenticationservice.utils.MessageUtils;
import com.rabbitmq.client.Channel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final RoleService roleService;
    private final LoginProfileService loginProfileService;
    private final MessageSenderService messageSenderService;
    private final MessageUtils messageUtils;

    private final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

    private static final int MAX_RETRIES = 6;

    /* Public Methods */
    @RabbitListener(queues = "roles-init-queue", containerFactory = "autoAckListenerContainerFactory")
    public void receiveRolesInitMessage(Message originalMessage) {
        String getAllRolesPath = new String(originalMessage.getBody());
        Integer retryCount = (Integer) originalMessage.getMessageProperties().getHeaders().get("x-retry-count");

        logger.info("Message received on startup of a service to init its roles: " + getAllRolesPath);
        try {
            if ("/api/directory/v1/roles".equals(getAllRolesPath) && retryCount == null) {
                try {
                    logger.info(
                            "Begginning sleep, for demonstration purpose, you have 20 seconds to pause Springboot Directory container");
                    Thread.sleep(20000);
                } catch (Exception e) {
                    logger.error("Failed to sleep for the demonstration", e);
                }
            }
            // try to save all microservice roles
            roleService.saveAllMicroserviceRoles(getAllRolesPath);
            logger.info("Roles successfully initialised");
        } catch (RestClientException e) {
            logger.error("Roles initialisation failed", e);
            messageSenderService.sendToDeadLetterQueueFrom(originalMessage, "roles-init-queue");
        }

    }

    @RabbitListener(queues = "role-activation-queue", containerFactory = "autoAckListenerContainerFactory")
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

    @RabbitListener(queues = "employee-email-queue", containerFactory = "autoAckListenerContainerFactory")
    public void receiveEmployeeEmailMessage(String message, @Header("amqp_receivedRoutingKey") String routingKey) {
        switch (routingKey) {
            case "employee.email.update":
                receiveEmployeeEmailUpdate(message);
                break;
        }
    }

    @RabbitListener(queues = "dead-letter-queue", containerFactory = "manualAckListenerContainerFactory")
    public void receiveDeadLetterMessage(Message failedMessage,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Channel channel) {

        String originalQueue = failedMessage.getMessageProperties().getHeader("x-original-queue");
        Integer retryCount = (Integer) failedMessage.getMessageProperties().getHeader("x-retry-count");

        if (retryCount == null) {
            retryCount = 0;
        }

        logger.info("Received a message in DLQ, with original queue: " + originalQueue + ", and already retried: "
                + retryCount + " times.");

        // Increment retry count for the next attempt
        retryCount++;
        failedMessage.getMessageProperties().setHeader("x-retry-count", retryCount);

        // Optionally, you can decide on a maximum retry count here
        if (retryCount <= MAX_RETRIES) {
            long delay = messageUtils.calculateExponentialBackoff(retryCount);

            logger.info("Exponential backoff duration to sleep is now: " + delay);

            messageSenderService.resendMessageWithDelay(failedMessage, originalQueue, delay);
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error(
                    "Attained maximum number of retries, we are not sending the message anymore, it will therefore live forever in the DLQ");
        }
    }

    /* Private Methods */
    private void receiveEmployeeEmailUpdate(String getEmployeeEmailById) {
        logger.info("Message received to update a login-profile email: " + getEmployeeEmailById);
        loginProfileService.updateLoginProfileEmail(getEmployeeEmailById);
        logger.info("Email successfully updated");
    }

}
