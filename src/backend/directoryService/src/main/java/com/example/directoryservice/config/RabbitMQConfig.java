package com.example.directoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

    @Bean
    public DirectExchange rolesExchange() {
        return new DirectExchange("roles-exchange");
    }

    @Bean
    public FanoutExchange loginProfilesSecExchange() {
        return new FanoutExchange("login-profiles-sec-exchange");
    }

    @Bean
    public TopicExchange loginProfilesInfoExchange() {
        return new TopicExchange("login-profiles-info-exchange");
    }

    @Bean
    public Queue loginProfileSecQueue() {
        return new Queue("login-profile-sec-queue");
    }

    @Bean
    public Binding loginProfileSecBinding(Queue loginProfileSecQueue,
                                      Exchange loginProfilesSecExchange) {
        return BindingBuilder.bind(loginProfileSecQueue)
                .to(loginProfilesSecExchange)
                .with("*")
                .noargs();
    }
}
