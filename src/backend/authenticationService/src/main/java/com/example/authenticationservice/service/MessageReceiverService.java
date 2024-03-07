package com.example.authenticationservice.service;

import com.example.authenticationservice.annotation.LogMessageReceived;
import com.example.authenticationservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

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
    private final CustomLogger logger;

    private static final int MAX_RETRIES = 6;

    /* Public Methods */
    @RabbitListener(queues = "roles-init-queue", containerFactory = "autoAckListenerContainerFactory")
    @LogMessageReceived(tag = CustomLogger.TAG_ROLES,
            deliveryMethod = "Unicast", queue = "roles-init-queue")
    public void receiveRolesInitMessage(Message message) {
        String getAllRolesPath = new String(message.getBody());
        Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get("x-retry-count");
        String methodName = "receiveRolesInitMessage";
        try {
            if ("/api/directory/v1/roles".equals(getAllRolesPath) && retryCount == null) {
                try {
                    String infoMessage = "Beginning sleep, for demonstration purpose, " +
                            "you have 20 seconds to pause Springboot Directory container.";
                    // logger.info(infoMessage, CustomLogger.TAG_ROLES, methodName,
                    // CustomLogger.METHOD_TYPE_MESSAGE_RECEPTION);
                    // Thread.sleep(20000);
                } catch (Exception ignored) {
                }
            }
            // try to save all microservice roles
            roleService.saveAllMicroserviceRoles(getAllRolesPath);
        } catch (RestClientException e) {
            logger.error("Roles initialisation failed",
                    CustomLogger.TAG_ROLES, methodName);
            messageSenderService.sendToDeadLetterQueueFrom(message, "roles-init-queue");
        }

    }

    @RabbitListener(queues = "role-activation-queue", containerFactory = "autoAckListenerContainerFactory")
    @LogMessageReceived(tag = CustomLogger.TAG_ROLES,
            deliveryMethod = "Unicast", queue = "role-activation-queue")
    public void receiveRoleActivationMessage(Message message) {
        String getRoleByNamePath = new String(message.getBody());
        try {
            // try to save microservice role
            roleService.saveMicroserviceRole(getRoleByNamePath);
        } catch (Exception ignored) {
            String methodName = "receiveRoleActivationMessage";
            logger.error("Role activation status set failed",
                    CustomLogger.TAG_ROLES, methodName);
        }
    }

    @RabbitListener(queues = "employee-email-queue", containerFactory = "autoAckListenerContainerFactory")
    @LogMessageReceived(tag = CustomLogger.TAG_USERS,
            deliveryMethod = "Multicast", queue = "employee-email-queue")
    public void receiveEmployeeEmailMessage(Message message, @Header("amqp_receivedRoutingKey") String routingKey) {
        String body = new String(message.getBody());
        if (routingKey.equals("employee.email.update"))
            receiveEmployeeEmailUpdate(body);
    }

    @RabbitListener(queues = "dead-letter-queue", containerFactory = "manualAckListenerContainerFactory")
    @LogMessageReceived(tag = CustomLogger.TAG_MESSAGE,
            deliveryMethod = "Unicast", queue = "dead-letter-queue")
    public void receiveDeadLetterMessage(Message failedMessage,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Channel channel) {

        String originalQueue = failedMessage.getMessageProperties().getHeader("x-original-queue");
        Integer retryCount = failedMessage.getMessageProperties().getHeader("x-retry-count");

        if (retryCount == null) {
            retryCount = 0;
        }

        String methodName = "receiveDeadLetterMessage";
        String routingPattern = "roles.init";
        String deliveryMethod = "Unicast";
        String errorMessage = "Message received in the dead-letter-queue (original queue: " + originalQueue + "). " +
                        "Service already retries " +  retryCount + " times.";

        // Increment retry count for the next attempt
        retryCount++;
        failedMessage.getMessageProperties().setHeader("x-retry-count", retryCount);

        // Optionally, you can decide on a maximum retry count here
        if (retryCount <= MAX_RETRIES) {
            long delay = messageUtils.calculateExponentialBackoff(retryCount);
            errorMessage +=
                    " Calculated exponential backoff delay before sending the message again will be " + delay + "ms.";
            logger.error(errorMessage, methodName, routingPattern, deliveryMethod, retryCount, delay);
            messageSenderService.resendMessageWithDelay(failedMessage, originalQueue, delay);
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException ignored) {
            }
        } else {
            errorMessage += " Maximum number of retries reached, message will not be send anymore. " +
                    "It will therefore live forever in the dead letter queue";
            logger.error(errorMessage, methodName, routingPattern, deliveryMethod, retryCount);
        }
    }

    /* Private Methods */
    private void receiveEmployeeEmailUpdate(String getEmployeeEmailById)
    {
        try {
            // try to update employee email
            loginProfileService.updateLoginProfileEmail(getEmployeeEmailById);
        } catch (Exception ignored) {
            String methodName = "receiveEmployeeEmailUpdate";
            logger.error("User email update failed",
                    CustomLogger.TAG_USERS, methodName);
        }
    }

}
