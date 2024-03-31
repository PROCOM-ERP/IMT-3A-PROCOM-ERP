package com.example.authenticationservice.annotation;

import com.example.authenticationservice.utils.ControllerExceptionHandler;
import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging errors within the application's controllers. It is utilized as part of the Aspect-Oriented Programming (AOP)
 * approach to handle exceptions systematically across the application. This annotation enables the definition of the HTTP status and an optional tag
 * to categorize the error, which can be used in logging and error handling strategies.
 * <p>
 * When applied to a method, it advises the {@link ControllerExceptionHandler} to log the specified details of the encountered exception.
 * This allows for a centralized way of managing error logging and response statuses.
 *
 * @since 1.0.0 (2024-02-22)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.0.0 (2024-02-22)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogError {

    /**
     * Defines the {@link HttpStatus} to be used when responding to the client in the event of an exception.
     * This status code is directly used in the HTTP response to the client.
     *
     * @return The HTTP status code to be applied.
     * @since 1.0.0
     */
    HttpStatus httpStatus();

    /**
     * Optional tag for categorizing or identifying the error. This can be utilized for filtering or grouping error logs.
     * By default, this is set to "Error".
     *
     * @return The tag associated with the error log.
     * @since 1.0.0
     */
    String tag() default "Error";

}
