package com.example.authenticationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({ "securityConfig", "restConfig" })
public class RabbitMQConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    /* Queues */

    @Bean
    public Queue roleActivationQueue() {
        return new Queue("role-activation-queue");
    }

    @Bean
    public Queue rolesInitQueue() {
        return QueueBuilder.durable("roles-init-queue")
                .withArgument("x-dead-letter-exchange", "dead-letter-exchange")
                .withArgument("x-dead-letter-routing-key", "dead.letter")
                .build();
    }

    @Bean
    public Queue employeeEmailQueue() {
        return new Queue("employee-email-queue");
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dead-letter-queue").build();
    }

    /* Exchanges */

    @Bean
    public DirectExchange rolesDirectExchange() {
        return new DirectExchange("roles-direct-exchange");
    }

    @Bean
    public FanoutExchange rolesFanoutExchange() {
        return new FanoutExchange("roles-fanout-exchange");
    }

    @Bean
    public FanoutExchange loginProfilesSecExchange() {
        return new FanoutExchange("login-profiles-sec-exchange");
    }

    @Bean
    public TopicExchange employeesExchange() {
        return new TopicExchange("employees-exchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dead-letter-exchange");
    }

    /* Bindings */

    @Bean
    public Binding rolesInitBinding(Queue rolesInitQueue,
            Exchange rolesDirectExchange) {
        return BindingBuilder.bind(rolesInitQueue)
                .to(rolesDirectExchange)
                .with("roles.init")
                .noargs();
    }

    @Bean
    public Binding roleActivationBinding(Queue roleActivationQueue,
            Exchange rolesDirectExchange) {
        return BindingBuilder.bind(roleActivationQueue)
                .to(rolesDirectExchange)
                .with("role.activation")
                .noargs();
    }

    @Bean
    public Binding employeeEmailBinding(Queue employeeEmailQueue,
            Exchange employeesExchange) {
        return BindingBuilder.bind(employeeEmailQueue)
                .to(employeesExchange)
                .with("employee.email.*")
                .noargs();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue,
            Exchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("dead.letter")
                .noargs();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory autoAckListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
