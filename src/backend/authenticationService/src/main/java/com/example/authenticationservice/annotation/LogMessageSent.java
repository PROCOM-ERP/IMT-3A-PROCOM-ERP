package com.example.authenticationservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging the act of sending AMQP messages with specific routing patterns and delivery methods. This annotation
 * is intended for methods that perform message sending operations, enabling the inclusion of a description of the operation, a categorization tag,
 * and details about the routing pattern and delivery method used. This facilitates a detailed and structured logging mechanism for outgoing messages,
 * aiding in the monitoring, debugging, and analysis of message flows and their distribution mechanisms within the application.
 * <p>
 * The annotation's parameters provide insights into how messages are routed and delivered, supporting efficient troubleshooting and optimization
 * of message handling processes.
 *
 * @since 1.0.0 (2024-02-22)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.0.0 (2024-02-22)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMessageSent {

    /**
     * A description of the message sending operation. This provides context for the log entry, indicating the purpose or effect
     * of the message being sent, and can be particularly helpful in understanding the role of the operation within the application's workflow.
     *
     * @return The description of the message sending operation.
     * @since 1.0.0
     */
    String description();

    /**
     * Optional tag for categorizing or identifying the log entry related to the message sending operation. Tags can be used to
     * organize log entries, especially when managing or analyzing the transmission of messages across different parts of the application.
     * By default, this is left empty.
     *
     * @return The tag associated with the message sending log entry.
     * @since 1.0.0
     */
    String tag() default "";

    /**
     * Specifies the routing pattern used for the message. This detail is crucial for understanding how the message is directed
     * within the messaging infrastructure, affecting its eventual delivery and reception.
     *
     * @return The routing pattern of the sent message.
     * @since 1.0.0
     */
    String routingPattern();

    /**
     * Defines the delivery method of the sent message, such as unicast, multicast, or broadcast. This information complements
     * the routing pattern by further detailing how the message is distributed to its intended recipients.
     *
     * @return The delivery method of the message.
     * @since 1.0.0
     */
    String deliveryMethod();

}
