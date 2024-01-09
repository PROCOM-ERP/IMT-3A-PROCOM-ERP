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

  @Bean
  public Queue rolesEnableModifyQueue() {
    return new Queue("role-enable-modify-queue");
  }

  @Bean
  public Queue rolesInitQueue() {
    return new Queue("roles-init-queue");
  }

  @Bean
  public Queue rolesNewQueue() {
    return new Queue("roles-new-queue");
  }

  @Bean
  public Queue employeeEmailQueue() {
    return new Queue("employee-email-queue");
  }
  @Bean
  public Exchange rolesExchange() {
    return new DirectExchange("roles-exchange");
  }

  @Bean
  public Exchange employeeInfoExchange() {
    return new TopicExchange("employee-info-exchange");
  }

  @Bean
  public Binding rolesInitBinding(Queue rolesInitQueue,
                                  Exchange rolesExchange) {
    return BindingBuilder.bind(rolesInitQueue)
        .to(rolesExchange)
        .with("roles.init")
        .noargs();
  }

  @Bean
  public Binding rolesEnableModifyBinding(Queue rolesEnableModifyQueue,
                                          Exchange rolesExchange) {
    return BindingBuilder.bind(rolesEnableModifyQueue)
        .to(rolesExchange)
        .with("role.enable.modify")
        .noargs();
  }

  @Bean
  public Binding rolesNewBinding(Queue rolesNewQueue, Exchange rolesExchange) {
    return BindingBuilder.bind(rolesNewQueue)
        .to(rolesExchange)
        .with("roles.init")
        .noargs();
  }

  @Bean
  public Binding employeeEmailBinding(Queue employeeEmailQueue,
                                      Exchange employeeInfoExchange) {
    return BindingBuilder.bind(employeeEmailQueue)
        .to(employeeInfoExchange)
        .with("employees.email.*")
        .noargs();
  }
}
