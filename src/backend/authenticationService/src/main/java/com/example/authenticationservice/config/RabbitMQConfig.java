package com.example.authenticationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for RabbitMQ messaging within the application. It defines various beans for queues,
 * exchanges, and bindings that facilitate message-driven communication. Additionally, this configuration specifies
 * listener containers for message consumption with different acknowledgement modes.
 * This class depends on prior initialization of {@link SecurityConfig} and {@link RestConfig} to ensure that security
 * and REST functionalities are available for use with RabbitMQ components.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 1.1.0 (2024-03-31)
 */
@Configuration
@DependsOn({ "securityConfig", "restConfig" })
@RequiredArgsConstructor
public class RabbitMQConfig {

    /**
     * Connection factory for creating connections to the RabbitMQ server.
     * It is injected by Spring and used by the RabbitMQ client to manage message publishing and consumption.
     *
     * @since 1.1.0
     */
    private final ConnectionFactory connectionFactory;

    /* Queues */

    /**
     * Defines a queue for role activation messages. Messages in this queue are related to the activation of user roles.
     *
     * @return A {@link Queue} object for role activation.
     * @since 0.1.0
     */
    @Bean
    public Queue roleActivationQueue() {
        return new Queue("role-activation-queue");
    }

    /**
     * Creates a durable queue for initializing roles with dead-letter configuration. Messages failing to be processed
     * are sent to a dead-letter exchange.
     *
     * @return A durable {@link Queue} with dead-letter configuration for roles initialization.
     * @since 1.1.0
     */
    @Bean
    public Queue rolesInitQueue() {
        return QueueBuilder.durable("roles-init-queue")
                .withArgument("x-dead-letter-exchange", "dead-letter-exchange")
                .withArgument("x-dead-letter-routing-key", "dead.letter")
                .build();
    }

    /**
     * Configures a queue for employee email messages. This queue handles messages related to sending emails to employees.
     *
     * @return A {@link Queue} for employee email notifications.
     * @since 0.1.0
     */
    @Bean
    public Queue employeeEmailQueue() {
        return new Queue("employee-email-queue");
    }

    /**
     * Defines a durable queue for dead-letter messages. This queue stores messages that cannot be processed and need manual intervention.
     *
     * @return A durable {@link Queue} for dead-letter messages.
     * @since 1.1.0
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dead-letter-queue").build();
    }

    /* Exchanges */

    /**
     * Defines a direct exchange for routing role-related messages. Direct exchanges route messages
     * to queues based on a message routing key that matches the binding key.
     *
     * @return A {@link DirectExchange} for directing role-related messages.
     * @since 0.1.0
     */
    @Bean
    public DirectExchange rolesDirectExchange() {
        return new DirectExchange("roles-direct-exchange");
    }

    /**
     * Configures a fanout exchange for broadcasting role-related messages to all bound queues.
     * Fanout exchanges route messages to all bound queues without considering the routing key.
     *
     * @return A {@link FanoutExchange} for broadcasting role-related messages.
     * @since 0.1.0
     */
    @Bean
    public FanoutExchange rolesFanoutExchange() {
        return new FanoutExchange("roles-fanout-exchange");
    }

    /**
     * Creates a fanout exchange for distributing login profile security updates. Like other fanout exchanges,
     * it routes messages to all bound queues, ensuring broad dissemination of updates.
     *
     * @return A {@link FanoutExchange} for login profile security updates.
     * @since 0.1.0
     */
    @Bean
    public FanoutExchange loginProfilesSecExchange() {
        return new FanoutExchange("login-profiles-sec-exchange");
    }

    /**
     * Establishes a topic exchange for employee-related messages. Topic exchanges route messages to queues based on
     * wildcard matches between the routing key and the binding pattern.
     *
     * @return A {@link TopicExchange} for routing employee-related messages.
     * @since 0.1.0
     */
    @Bean
    public TopicExchange employeesExchange() {
        return new TopicExchange("employees-exchange");
    }

    /**
     * Sets up a direct exchange for dead-letter messages. This exchange facilitates the routing of failed or unprocessable
     * messages to a designated dead-letter queue for further inspection or processing.
     *
     * @return A {@link DirectExchange} for dead-letter messages.
     * @since 1.1.0
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dead-letter-exchange");
    }

    /* Bindings */

    /**
     * Binds the roles initialization queue to the roles direct exchange with a specific routing key.
     * This binding directs messages tagged with "roles.init" directly to the roles initialization queue.
     *
     * @param rolesInitQueue The roles initialization queue.
     * @param rolesDirectExchange The direct exchange for role-related messages.
     * @return A {@link Binding} object linking the queue and the exchange.
     * @since 1.1.0
     */
    @Bean
    public Binding rolesInitBinding(Queue rolesInitQueue,
            Exchange rolesDirectExchange) {
        return BindingBuilder.bind(rolesInitQueue)
                .to(rolesDirectExchange)
                .with("roles.init")
                .noargs();
    }

    /**
     * Creates a binding between the role activation queue and the roles direct exchange using a specific routing key.
     * This setup ensures that messages with "role.activation" as the routing key are routed to the role activation queue.
     *
     * @param roleActivationQueue The role activation queue.
     * @param rolesDirectExchange The direct exchange for role-related messages.
     * @return A {@link Binding} for the role activation messaging.
     * @since 0.1.0
     */
    @Bean
    public Binding roleActivationBinding(Queue roleActivationQueue,
            Exchange rolesDirectExchange) {
        return BindingBuilder.bind(roleActivationQueue)
                .to(rolesDirectExchange)
                .with("role.activation")
                .noargs();
    }

    /**
     * Associates the employee email queue with the employees topic exchange through a binding pattern.
     * This pattern allows for routing messages with a routing key matching "employee.email.*" to the employee email queue.
     *
     * @param employeeEmailQueue The queue for employee email messages.
     * @param employeesExchange The topic exchange for employee-related messages.
     * @return A {@link Binding} facilitating the routing of employee email messages.
     * @since 0.1.0
     */
    @Bean
    public Binding employeeEmailBinding(Queue employeeEmailQueue,
            Exchange employeesExchange) {
        return BindingBuilder.bind(employeeEmailQueue)
                .to(employeesExchange)
                .with("employee.email.*")
                .noargs();
    }

    /**
     * Configures a binding for the dead-letter queue to the dead-letter exchange using a dedicated routing key.
     * This binding is crucial for handling messages that cannot be processed and are routed to the dead-letter queue.
     *
     * @param deadLetterQueue The dead-letter queue.
     * @param deadLetterExchange The direct exchange for dead-letter messaging.
     * @return A {@link Binding} that links the dead-letter queue to the dead-letter exchange.
     * @since 1.1.0
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue,
            Exchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("dead.letter")
                .noargs();
    }

    /**
     * Configures a {@link SimpleRabbitListenerContainerFactory} for auto-acknowledgement mode. This container factory
     * is used to create listeners that automatically acknowledge messages upon successful processing.
     *
     * @return A {@link SimpleRabbitListenerContainerFactory} configured for auto acknowledgement mode.
     * @since 1.1.0
     */
    @Bean
    public SimpleRabbitListenerContainerFactory autoAckListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * Configures a {@link SimpleRabbitListenerContainerFactory} for manual-acknowledgement mode. This container factory
     * allows for manual control over the acknowledgement of messages, providing flexibility in handling message processing.
     *
     * @return A {@link SimpleRabbitListenerContainerFactory} configured for manual acknowledgement mode.
     * @since 1.1.0
     */
    @Bean
    public SimpleRabbitListenerContainerFactory manualAckListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

}
