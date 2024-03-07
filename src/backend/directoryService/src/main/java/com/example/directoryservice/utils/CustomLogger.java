package com.example.directoryservice.utils;

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
    public static final String TAG_ADDRESSES = "Addresses";
    public static final String TAG_ORGANISATIONS = "Organisations";

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

    public void error(Exception e, String tag, String methodName, HttpStatus httpStatus)
    {
        MDC.put("httpStatus", httpStatus.toString());
        MDC.put("stacktrace", Arrays.toString(e.getStackTrace()));
        error(e.getMessage(), tag, methodName);
    }

    public void error(String message, String tag, String methodName,
                      String routingPattern, String deliveryMethod, int retries, long delay)
    {
        MDC.put("amqpMessageSendDelay", String.valueOf(delay));
        error(message, tag, methodName, routingPattern, deliveryMethod, retries);
    }

    public void error(String message, String tag, String methodName,
                      String routingPattern, String deliveryMethod, int retries)
    {
        MDC.put("routingPattern", routingPattern);
        MDC.put("deliveryMethod", deliveryMethod);
        MDC.put("amqpMessageSendRetries", String.valueOf(retries));
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
