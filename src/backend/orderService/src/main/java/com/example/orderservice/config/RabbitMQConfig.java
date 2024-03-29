package com.example.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

    /* Queues */

    @Bean
    public Queue rolesNewQueueOrder() {
        return new Queue("roles-new-queue-order");
    }

    @Bean
    public Queue loginProfilesSecQueueOrder() {
        return new Queue("login-profiles-sec-queue-order");
    }

    @Bean
    public Queue employeeInfoOrderQueue() {
        return new Queue("employee-info-order-queue");
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
    public Binding rolesNewBinding(Queue rolesNewQueueOrder,
                                   Exchange rolesFanoutExchange) {
        return BindingBuilder.bind(rolesNewQueueOrder)
                .to(rolesFanoutExchange)
                .with("*")
                .noargs();
    }

    @Bean
    public Binding loginProfilesSecBinding(Queue loginProfilesSecQueueOrder,
                                           Exchange loginProfilesSecExchange) {
        return BindingBuilder.bind(loginProfilesSecQueueOrder)
                .to(loginProfilesSecExchange)
                .with("*")
                .noargs();
    }

    @Bean
    public Binding employeeInfoGetBinding(Queue employeeInfoOrderQueue,
                                          Exchange employeesDirectExchange) {
        return BindingBuilder.bind(employeeInfoOrderQueue)
                .to(employeesDirectExchange)
                .with("employee.info.order")
                .noargs();
    }
}
