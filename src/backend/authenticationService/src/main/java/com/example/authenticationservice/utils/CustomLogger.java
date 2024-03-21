package com.example.authenticationservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomLogger {

    /* Constants */
    public static final String METHOD_TYPE_SERVICE = "ServiceMethod";
    public static final String METHOD_TYPE_MESSAGE_SEND = "MessageSendingMethod";
    public static final String METHOD_TYPE_MESSAGE_RECEPTION = "MessageReceptionMethod";
    public static final String METHOD_TYPE_ERROR = "ErrorHandlingMethod";

    public static final String TAG_USERS = "Users";
    public static final String TAG_ROLES = "Roles";
    public static final String TAG_MESSAGE = "MessageIssues";

    /* Utils Beans */
    private final Logger logger = LoggerFactory.getLogger(CustomLogger.class);

    /* Public Methods */

    public void infoServiceMethod(String message, String tag,
                                  String methodName, long methodExecutionTime)
    {
        MDC.put("methodExecutionTime", String.valueOf(methodExecutionTime));
        info(message, tag, methodName, METHOD_TYPE_SERVICE);
    }

    public void infoMessageSendingMethod(String message, String tag,
                                         String methodName, String routingPattern, String deliveryMethod)
    {
        info(message, tag, methodName, METHOD_TYPE_MESSAGE_SEND, routingPattern, deliveryMethod);
    }

    public void infoMessageReceptionMethod(String message, String tag,
                                           String methodName, String routingPattern, String deliveryMethod,
                                           String sender, String queue, long amqpMessageReceptionTime)
    {
        MDC.put("amqpReceptionTime", String.valueOf(amqpMessageReceptionTime));
        MDC.put("sender", sender);
        MDC.put("queue", queue);
        info(message, tag, methodName, METHOD_TYPE_MESSAGE_RECEPTION, routingPattern, deliveryMethod);
    }

    public void infoLoginProfile(String message, String tag, String username, String password)
    {
        MDC.put("username", username);
        MDC.put("password", password);
        info(message, tag);
    }

    public void error(Exception e, String tag, String methodName, HttpStatus httpStatus)
    {
        MDC.put("stackTrace", Arrays.toString(e.getStackTrace()));
        error(e.getMessage(), tag, methodName, httpStatus);
    }

    public void error(String message, String methodName,
                      String routingPattern, String deliveryMethod, int retries, long delay)
    {
        MDC.put("amqpSendingDelay", String.valueOf(delay));
        error(message, methodName, routingPattern, deliveryMethod, retries);
    }

    public void error(String message, String methodName,
                      String routingPattern, String deliveryMethod, int retries)
    {
        MDC.put("routingPattern", routingPattern);
        MDC.put("deliveryMethod", deliveryMethod);
        MDC.put("amqpSendingRetries", String.valueOf(retries));
        error(message, TAG_MESSAGE, methodName);
    }

    public void error(String message, String tag, String methodName, HttpStatus httpStatus)
    {
        MDC.put("httpStatus", httpStatus.toString());
        error(message, tag, methodName);
    }

    public void error(String message, String tag, String methodName)
    {
        MDC.put("tag", tag);
        MDC.put("methodName", methodName);
        MDC.put("methodType", METHOD_TYPE_ERROR);
        logger.error(message);
        MDC.clear();
    }

    /* Private Methods */
    private void info(String message, String tag)
    {
        MDC.put("tag", tag);
        logger.info(message);
        MDC.clear();
    }

    public void info(String message, String tag,
                      String methodName, String methodType)
    {
        MDC.put("methodName", methodName);
        MDC.put("methodType", methodType);
        info(message, tag);
    }

    private void info(String message, String tag,
                      String methodName, String methodType,
                      String routingPattern, String deliveryMethod)
    {
        MDC.put("routingPattern", routingPattern);
        MDC.put("deliveryMethod", deliveryMethod);
        info(message, tag, methodName, methodType);
    }
}
