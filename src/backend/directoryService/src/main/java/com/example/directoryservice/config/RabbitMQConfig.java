package com.example.directoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

    @Bean
    public DirectExchange rolesExchange() {
        return new DirectExchange("roles-exchange");
    }

    @Bean
    public FanoutExchange employeeSecExchange() {
        return new FanoutExchange("employee-sec-exchange");
    }

    @Bean
    public TopicExchange employeeInfoExchange() {
        return new TopicExchange("employee-info-exchange");
    }

    @Bean
    public Queue employeeSecQueue() {
        return new Queue("employee-sec-queue");
    }

    @Bean
    public Binding employeeSecBinding(Queue employeeSecQueue,
                                      Exchange employeeSecExchange) {
        return BindingBuilder.bind(employeeSecQueue)
                .to(employeeSecExchange)
                .with("*")
                .noargs();
    }
}
