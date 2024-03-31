package com.example.authenticationservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging the execution time of methods within the application. It facilitates performance monitoring by logging
 * the time taken (in milliseconds) for annotated method executions. The annotation allows specifying a description of the method's purpose
 * and an optional tag for categorizing or filtering the log entries.
 * <p>
 * This annotation is particularly useful in identifying performance bottlenecks and ensuring that the application meets its performance
 * criteria by providing insights into the runtime behavior of its components.
 *
 * @since 1.0.0 (2024-02-22)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.0.0 (2024-02-22)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {

    /**
     * A description of the method being logged. This description can provide context for the log entry, indicating what the method
     * does or why its execution time is relevant.
     *
     * @return The description of the method's purpose or functionality.
     * @since 1.0.0
     */
    String description();

    /**
     * Optional tag for categorizing or identifying the log entry. Tags can be used to filter or group log entries based on the
     * method's role or the feature it is associated with.
     * By default, this is left empty.
     *
     * @return The tag associated with the log entry.
     * @since 1.0.0
     */
    String tag() default "";

}
