package com.example.authenticationservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging the reception of AMQP messages within specified queues. It is designed to annotate methods that are triggered
 * by the reception of a message, allowing for detailed logging including the delivery method and the queue from which the message was received.
 * The delivery method can be specified as unicast, multicast, or broadcast, providing insights into the nature of the message distribution.
 * <p>
 * This annotation aids in the monitoring and debugging of message-driven behaviors in the application, ensuring that message handling processes
 * are transparent and traceable.
 *
 * @since 1.0.0 (2024-02-22)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.0.0 (2024-02-22)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMessageReceived {

    /**
     * Optional tag for categorizing or identifying the log entry related to message reception. Tags can facilitate the filtering
     * or grouping of log entries, especially when monitoring or debugging the handling of messages across various parts of the application.
     * By default, this is left empty.
     *
     * @return The tag associated with the message reception log entry.
     * @since 1.0.0
     */
    String tag() default "";

    /**
     * Specifies the delivery method of the received message, which can be unicast, multicast, or broadcast. This detail enhances
     * the log's usefulness by indicating the scope of message distribution at the time of reception.
     *
     * @return The delivery method of the message.
     * @since 1.0.0
     */
    String deliveryMethod();

    /**
     * The name of the queue from which the message was received. Identifying the queue contributes to a comprehensive understanding
     * of the message flow within the application, aiding in troubleshooting and performance analysis.
     *
     * @return The name of the queue involved in the message reception.
     * @since 1.0.0
     */
    String queue();

}
