package com.example.directoryservice.service;

import com.example.directoryservice.model.Path;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import com.example.directoryservice.utils.MessageUtils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderService implements CommandLineRunner {

    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange rolesDirectExchange;
    private final TopicExchange employeesExchange;
    private final MessageUtils messageUtils;

    private final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);

    private static final int MAX_RETRIES = 10; // Example max retries

    @Override
    public void run(String... args) {
        logger.info("Sending message about service initialisation to retrieve its roles...");
        String resource = Path.ROLES;
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);

        // Setting the init roles messages to mandatory, as well as return message
        // format
        // We will have more information if the message is sent back

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
        RabbitTemplate mandatoryTemplate = rabbitTemplate;
        mandatoryTemplate.setMandatory(true);

        MessageConverter messageConverter = mandatoryTemplate.getMessageConverter();
        Message message = messageConverter.toMessage(path, null);

        sendWithRetry(rolesDirectExchange.getName(), "roles.init", message, messagePostProcessor, 0);
    }

    public void sendRoleActivationMessage(String roleName) {
        logger.info("Sending message to inform about a change on role activation status...");
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesDirectExchange.getName(), "role.activation", path);
        logger.info("Message sent");
    }

    public void sendEmployeeEmailUpdateMessage(String idEmployee) {
        logger.info("Sending message to inform about a change on employee email...");
        String resource = String.format("%s/%s%s", Path.EMPLOYEES, idEmployee, Path.EMAIL);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(employeesExchange.getName(), "employee.email.update", path);
        logger.info("Message sent");
    }

    public void sendWithRetry(String exchange, String routingKey, Message data, MessagePostProcessor postProcessor,
            int retryCount) {
        rabbitTemplate.setMandatory(true);

        try {
            Object response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, data);
            if (response != null) {
                logger.info("Reply received for message sent to routing key: " + routingKey);
            } else {
                // it's normal not to receive a message, auth service's response need to be
                // configured
                logger.info(
                        "No reply received for message sent to routing key: " + routingKey + ", but message was sent.");
            }
        } catch (AmqpMessageReturnedException e) {
            logger.error("Message returned with reply code: " + e.getReplyCode() +
                    ", reply text: " + e.getReplyText() +
                    ", exchange: " + e.getExchange() +
                    ", routingKey: " + e.getRoutingKey());
            if (retryCount < MAX_RETRIES) {
                long delay = messageUtils.calculateExponentialBackoff(retryCount);
                try {
                    logger.info("Waiting for " + delay + "ms before retrying...");
                    Thread.sleep(delay); // Consider using scheduled tasks for non-blocking delay
                    logger.info("Retrying message send to it's original queue: " + routingKey);
                    retryCount++;
                    sendWithRetry(exchange, routingKey, e.getReturnedMessage(), postProcessor, retryCount);
                } catch (InterruptedException iException) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry delay interrupted", iException);
                } catch (Exception exception) {
                    logger.error("Error during message resend", exception);
                }
            } else {
                logger.error("Max retries reached. Giving up on sending message.");
            }
        }
    }

}
