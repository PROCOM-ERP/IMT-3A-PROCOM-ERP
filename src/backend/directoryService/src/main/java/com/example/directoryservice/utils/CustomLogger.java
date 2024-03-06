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

    public static final String TAG_USERS = "Users";
    public static final String TAG_ROLES = "Roles";
    public static final String TAG_ADDRESSES = "Addresses";
    public static final String TAG_ORGANISATIONS = "Organisations";

    /* Utils Beans */
    private final Logger logger = LoggerFactory.getLogger(CustomLogger.class);

    /* Public Methods */

    public void infoServiceMethod(String message, String tag,
                                  String methodName, long methodExecutionTime)
    {
        info(message, tag, methodName, METHOD_TYPE_SERVICE, methodExecutionTime);
    }

    public void infoMessageSendingMethod(String message, String tag,
                                         String methodName, long methodExecutionTime,
                                         long amqpMessageReceptionTime,
                                         String routingPattern, String deliveryMethod)
    {
        info(message, tag, methodName, METHOD_TYPE_MESSAGE_SEND, methodExecutionTime,
                amqpMessageReceptionTime, routingPattern, deliveryMethod);
    }

    public void infoMessageReceptionMethod(String message, String tag,
                                           String methodName, long methodExecutionTime,
                                           long amqpMessageReceptionTime,
                                           String routingPattern, String deliveryMethod,
                                           String sender, String queue)
    {
        MDC.put("sender", sender);
        MDC.put("queue", queue);
        info(message, tag, methodName, METHOD_TYPE_MESSAGE_RECEPTION, methodExecutionTime,
                amqpMessageReceptionTime, routingPattern, deliveryMethod);
    }

    public void error(Exception e, String tag, HttpStatus httpStatus)
    {
        MDC.put("tag", tag);
        MDC.put("httpStatus", httpStatus.toString());
        MDC.put("stacktrace", Arrays.toString(e.getStackTrace()));
        logger.error(e.getMessage());
        MDC.clear();
    }

    public void error(Exception e, String tag)
    {
        error(e, tag, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /* Private Methods */
    private void info(String message, String tag)
    {
        MDC.put("tag", tag);
        logger.info(message);
        MDC.clear();
    }

    private void info(String message, String tag,
                      String methodName, String methodType,
                      long methodExecutionTime)
    {
        MDC.put("methodName", methodName);
        MDC.put("methodType", methodType);
        MDC.put("methodExecutionTime", String.valueOf(methodExecutionTime));
        info(message, tag);
    }

    private void info(String message, String tag,
                      String methodName, String methodType,
                      long methodExecutionTime, long amqpMessageReceptionTime,
                      String routingPattern, String deliveryMethod)
    {
        MDC.put("amqpMessageReceptionTime", String.valueOf(amqpMessageReceptionTime));
        MDC.put("routingPattern", routingPattern);
        MDC.put("deliveryMethod", deliveryMethod);
        info(message, tag, methodName, methodType, methodExecutionTime);
    }
}
