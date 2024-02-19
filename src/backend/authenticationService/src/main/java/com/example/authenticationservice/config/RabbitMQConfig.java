package com.example.authenticationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

    /* Queues */

    @Bean
    public Queue roleActivationQueue() {
        return new Queue("role-activation-queue");
    }

    @Bean
    public Queue rolesInitQueue() {
        return new Queue("roles-init-queue");
    }

    @Bean
    public Queue employeeEmailQueue() {
        return new Queue("employee-email-queue");
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
}
