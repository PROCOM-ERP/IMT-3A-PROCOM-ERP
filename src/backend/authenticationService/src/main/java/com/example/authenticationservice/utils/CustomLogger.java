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
                                           String methodName, long amqpMessageReceptionTime,
                                           String routingPattern, String deliveryMethod,
                                           String sender, String queue)
    {
        MDC.put("amqpMessageReceptionTime", String.valueOf(amqpMessageReceptionTime));
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
        MDC.put("tag", tag);
        MDC.put("methodName", methodName);
        MDC.put("methodType", METHOD_TYPE_ERROR);
        MDC.put("httpStatus", httpStatus.toString());
        MDC.put("stacktrace", Arrays.toString(e.getStackTrace()));
        logger.error(e.getMessage());
        MDC.clear();
    }

    public void error(Exception e, String tag, String methodName)
    {
        error(e, tag, methodName, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* Private Methods */
    private void info(String message, String tag)
    {
        MDC.put("tag", tag);
        logger.info(message);
        MDC.clear();
    }

    private void info(String message, String tag,
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
