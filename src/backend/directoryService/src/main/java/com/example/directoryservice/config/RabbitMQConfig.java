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
    public Queue loginProfileSecQueue() {
        return new Queue("login-profile-sec-queue");
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
    public TopicExchange loginProfilesInfoExchange() {
        return new TopicExchange("login-profiles-info-exchange");
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
    public Binding loginProfileSecBinding(Queue loginProfileSecQueue,
                                          Exchange loginProfilesSecExchange) {
        return BindingBuilder.bind(loginProfileSecQueue)
                .to(loginProfilesSecExchange)
                .with("*")
                .noargs();
    }
}
