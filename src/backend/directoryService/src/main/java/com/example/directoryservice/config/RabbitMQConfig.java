package com.example.directoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Bean
  public Exchange employeeExchange() {
    return new DirectExchange("employee-exchange");
  }

  @Bean
  public Exchange employeeSecExchange() {
    return new FanoutExchange("employee-sec-exchange");
  }

  @Bean
  public Exchange employeeInfoExchange() {
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
