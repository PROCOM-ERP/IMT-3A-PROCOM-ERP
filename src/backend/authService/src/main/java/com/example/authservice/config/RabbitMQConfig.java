package com.example.authservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Bean
  public Queue helloQueue() {
    return new Queue("hello-queue");
  }

  @Bean
  public Exchange helloExchange() {
    return new DirectExchange("hello-exchange");
  }

  @Bean
  public Binding helloBinding(Queue helloQueue, Exchange helloExchange) {
    return BindingBuilder.bind(helloQueue)
        .to(helloExchange)
        .with("hello-key")
        .noargs();
  }
}
