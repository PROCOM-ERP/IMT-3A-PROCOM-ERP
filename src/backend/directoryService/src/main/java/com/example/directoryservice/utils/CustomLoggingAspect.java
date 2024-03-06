package com.example.directoryservice.utils;

import com.example.directoryservice.annotation.LogError;
import com.example.directoryservice.annotation.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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

    @Around("@annotation(com.example.directoryservice.annotation.LogExecutionTime)")
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

    @After("@annotation(com.example.directoryservice.annotation.LogError)")
    public void logError(@NonNull JoinPoint joinPoint)
    {
        // build method information
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        LogError methodAnnotation = methodSignature.getMethod()
                .getAnnotation(LogError.class);
        HttpStatus httpStatus = methodAnnotation.httpStatus();
        String tag = methodAnnotation.tag();

        // retrieve Exception and log the error
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) { // retrieve Exception param
            if (arg instanceof Exception e) {
                logger.error(e, tag, httpStatus);
                break;
            }
        }
    }
}
