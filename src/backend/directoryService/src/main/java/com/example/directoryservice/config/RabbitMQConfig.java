package com.example.directoryservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Bean
  public RabbitTemplate rabbitTemplate() {
    return new RabbitTemplate();
  }

  @Bean
  public Exchange rolesExchange() {
    return new DirectExchange("roles-exchange");
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
