package com.example.directoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

    /* Queues */

    @Bean
    public Queue rolesNewQueue() {
        return new Queue("roles-new-queue");
    }

    @Bean
    public Queue loginProfilesSecQueue() {
        return new Queue("login-profiles-sec-queue");
    }

    @Bean
    public Queue employeeInfoGetDirectoryQueue() {
        return new Queue("employee-info-get-queue");
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
    public DirectExchange employeesDirectExchange() {
        return new DirectExchange("employees-direct-exchange");
    }

    @Bean
    public TopicExchange employeesExchange() {
        return new TopicExchange("employees-exchange");
    }

    /* Bindings */

    @Bean
    public Binding rolesNewBinding(Queue rolesNewQueue,
                                   Exchange rolesFanoutExchange) {
        return BindingBuilder.bind(rolesNewQueue)
                .to(rolesFanoutExchange)
                .with("*")
                .noargs();
    }

    @Bean
    public Binding loginProfilesSecBinding(Queue loginProfilesSecQueue,
                                           Exchange loginProfilesSecExchange) {
        return BindingBuilder.bind(loginProfilesSecQueue)
                .to(loginProfilesSecExchange)
                .with("*")
                .noargs();
    }

    @Bean
    public Binding employeeInfoGetBinding(Queue employeeInfoGetDirectoryQueue,
                                          Exchange employeesDirectExchange) {
        return BindingBuilder.bind(employeeInfoGetDirectoryQueue)
                .to(employeesDirectExchange)
                .with("employee.info.get")
                .noargs();
    }
}
