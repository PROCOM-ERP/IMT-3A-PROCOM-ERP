package com.example.orderservice.utils;

import com.example.orderservice.annotation.LogError;
import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.annotation.LogMessageReceived;
import com.example.orderservice.annotation.LogMessageSent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CustomLoggingAspect {

    /* Utils Beans */
    private final CustomLogger logger;

    /* Public Methods */

    @Around("@annotation(com.example.orderservice.annotation.LogExecutionTime)")
    public Object logExecutionTime(@NonNull ProceedingJoinPoint joinPoint) throws
            Throwable
    {
        // execute the method and measure time execution
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(); // Proceed with the method execution.
        long executionTime = System.currentTimeMillis() - start;

        // build and log method information
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        LogExecutionTime methodAnnotation = methodSignature.getMethod()
                .getAnnotation(LogExecutionTime.class);
        String methodDescription = methodAnnotation.description();
        String tag = methodAnnotation.tag();
        logger.infoServiceMethod(methodDescription, tag, methodName, executionTime);

        // return the value returned by the processed method
        return proceed;
    }

    @Before("@annotation(com.example.orderservice.annotation.LogMessageSent)")
    public void logMessageSent(@NonNull JoinPoint joinPoint)
    {
        // build and log method information
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        LogMessageSent methodAnnotation = methodSignature.getMethod()
                .getAnnotation(LogMessageSent.class);
        String methodDescription = methodAnnotation.description();
        String routingPattern = methodAnnotation.routingPattern();
        String deliveryMethod = methodAnnotation.deliveryMethod();
        String tag = methodAnnotation.tag();
        logger.infoMessageSendingMethod(methodDescription, tag, methodName, routingPattern, deliveryMethod);
    }

    @Before("@annotation(com.example.orderservice.annotation.LogMessageReceived)")
    public void logMessageReceived(@NonNull JoinPoint joinPoint)
    {
        // build and log method information
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        LogMessageReceived methodAnnotation = methodSignature.getMethod()
                .getAnnotation(LogMessageReceived.class);
        String deliveryMethod = methodAnnotation.deliveryMethod();
        String queue = methodAnnotation.queue();
        String tag = methodAnnotation.tag();

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Message message) {
                logMessageReceived(tag, methodName, message, deliveryMethod, queue);
                break;
            }
        }
    }

    @After("@annotation(com.example.orderservice.annotation.LogError)")
    public void logError(@NonNull JoinPoint joinPoint)
    {
        // build method information
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        LogError methodAnnotation = methodSignature.getMethod()
                .getAnnotation(LogError.class);
        HttpStatus httpStatus = methodAnnotation.httpStatus();
        String tag = methodAnnotation.tag();

        // retrieve Exception and log the error
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) { // retrieve Exception param
            if (arg instanceof Exception e) {
                logger.error(e, tag, methodName, httpStatus);
                break;
            }
        }
    }

    /* Private Methods */

    private void logMessageReceived(String tag, String methodName,
                                    @NonNull Message message, String deliveryMethod, String queue)
    {
        // get message properties from header
        String methodDescription = message.getMessageProperties().getHeader("description");
        String routingPattern = message.getMessageProperties().getHeader("routingPattern");
        String sender = message.getMessageProperties().getHeader("sender");

        // calculate reception time
        Long sendingTimestamp = message.getMessageProperties().getHeader("systemTimeMillis");
        long receptionTime = System.currentTimeMillis() - sendingTimestamp;

        // log received message information
        logger.infoMessageReceptionMethod(methodDescription, tag, methodName,
                routingPattern, deliveryMethod, sender, queue, receptionTime);
    }
}
