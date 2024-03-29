package com.example.directoryservice.service;

import com.example.directoryservice.annotation.LogMessageSent;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import com.example.directoryservice.utils.CustomLogger;
import com.example.directoryservice.utils.MessageUtils;

import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService implements CommandLineRunner {

    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final RabbitTemplate rabbitTemplate;
    @Qualifier("rolesDirectExchange")
    private final DirectExchange rolesDirectExchange;
    @Qualifier("employeesDirectExchange")
    private final DirectExchange employeesDirectExchange;
    private final TopicExchange employeesExchange;
    private final MessageUtils messageUtils;
    private final CustomLogger logger;

    @Autowired
    public MessageSenderService(CustomHttpRequestBuilder customHttpRequestBuilder,
                                RabbitTemplate rabbitTemplate,
                                DirectExchange rolesDirectExchange,
                                DirectExchange employeesDirectExchange,
                                TopicExchange employeesExchange,
                                MessageUtils messageUtils,
                                CustomLogger logger)
    {
        this.customHttpRequestBuilder = customHttpRequestBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.rolesDirectExchange = rolesDirectExchange;
        this.employeesDirectExchange = employeesDirectExchange;
        this.employeesExchange = employeesExchange;
        this.messageUtils = messageUtils;
        this.logger = logger;
    }

    @Value("${security.service.name}")
    private String sender; // message sender is the service itself

    private static final int MAX_RETRIES = 10; // Example max retries

    @Override
    @LogMessageSent(tag = CustomLogger.TAG_ROLES,
            routingPattern = "roles.init",
            deliveryMethod = "Unicast",
            description = "Message sent to inform the authentication service that a service roles have to be retrieved.")
    public void run(String... args) {
        String description = "Message received to retrieve " + sender + " service roles.";
        String routingPattern = "roles.init";
        String resource = Path.ROLES;
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);

        // Setting the init roles messages to mandatory, as well as return message
        // format
        // We will have more information if the message is sent back

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setHeader("sender", sender);
            message.getMessageProperties().setHeader("routingPattern", routingPattern);
            message.getMessageProperties().setHeader("description", description);
            message.getMessageProperties().setHeader("systemTimeMillis", System.currentTimeMillis());
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
        RabbitTemplate mandatoryTemplate = rabbitTemplate;
        mandatoryTemplate.setMandatory(true);

        MessageConverter messageConverter = mandatoryTemplate.getMessageConverter();
        Message message = messageConverter.toMessage(path, null);

        sendWithRetry(rolesDirectExchange.getName(), routingPattern, message, messagePostProcessor, 0);
    }

    @LogMessageSent(tag = CustomLogger.TAG_ROLES,
            routingPattern = "role.activation",
            deliveryMethod = "Unicast",
            description = "Message sent to inform the authentication service " +
                    "that a service role activation status has changed.")
    public void sendRoleActivationMessage(String roleName) {
        String description = "Message received to set " + sender + " service role " +  roleName + " activation status.";
        String routingPattern = "role.activation";
        String resource = String.format("%s/%s%s", Path.ROLES, roleName, Path.ACTIVATION);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(rolesDirectExchange.getName(), routingPattern, path,
                getMessageCustomHeaders(description, routingPattern));
    }

    @LogMessageSent(tag = CustomLogger.TAG_USERS,
            routingPattern = "employee.email.update",
            deliveryMethod = "Multicast",
            description = "Message sent to inform subscribed services that a user email has changed.")
    public void sendEmployeeEmailUpdateMessage(String idEmployee) {
        String description = "Message received to set user " +  idEmployee + " email.";
        String routingPattern = "employee.email.update";
        String resource = String.format("%s/%s%s", Path.EMPLOYEES, idEmployee, Path.EMAIL);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(employeesExchange.getName(), routingPattern, path,
                getMessageCustomHeaders(description, routingPattern));
    }

    @LogMessageSent(tag = CustomLogger.TAG_ORDERS,
            routingPattern = "employee.info.order",
            deliveryMethod = "Unicast",
            description = "Message sent to inform the order service that user information are available in this service.")
    public void sendEmployeeInfoOrder(String idEmployee) {
        String description = "Message received to inform that user " + idEmployee +
                " information are available in a service.";
        String routingPattern = "employee.info.order";
        String resource = String.format("%s/%s", Path.EMPLOYEES, idEmployee);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(employeesDirectExchange.getName(), routingPattern, path,
                getMessageCustomHeaders(description, routingPattern));
    }

    public void sendWithRetry(String exchange, String routingKey, Message data,
                              MessagePostProcessor postProcessor, int retryCount)
    {
        rabbitTemplate.setMandatory(true);
        try {
            rabbitTemplate.convertSendAndReceive(exchange, routingKey, data, postProcessor);
            // it's normal not to receive a message, authentication service's response needs to be configured
        } catch (AmqpMessageReturnedException e) {
            String methodName = "sendWithRetry";
            String deliveryMethod = "Unicast";
            String errorMessage = "Message returned with reply code: " + e.getReplyCode() +
                    ", reply text: " + e.getReplyText() +
                    ", exchange: " + e.getExchange() +
                    ", routingKey: " + e.getRoutingKey();
            if (retryCount < MAX_RETRIES) {
                long delay = messageUtils.calculateExponentialBackoff(retryCount);
                try {
                    retryCount++;
                    errorMessage += ". Try to send it again with delay (" + delay + "ms).";
                    logger.error(errorMessage, methodName, routingKey, deliveryMethod, retryCount, delay);
                    Thread.sleep(delay); // Consider using scheduled tasks for non-blocking delay
                    sendWithRetry(exchange, routingKey, e.getReturnedMessage(), postProcessor, retryCount);
                } catch (InterruptedException iException) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                }
            } else {
                errorMessage += ". Max retries reached. Giving up on sending message.";
                logger.error(errorMessage, methodName, routingKey, deliveryMethod, retryCount);
            }
        }
    }

    /* Private Methods */
    private MessagePostProcessor getMessageCustomHeaders(String description, String routingPattern)
    {
        return message -> {
            message.getMessageProperties().setHeader("sender", sender);
            message.getMessageProperties().setHeader("routingPattern", routingPattern);
            message.getMessageProperties().setHeader("description", description);
            message.getMessageProperties().setHeader("systemTimeMillis", System.currentTimeMillis());
            return message;
        };
    }
}
