package com.example.authenticationservice.config;

import org.springframework.amqp.core.*;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"securityConfig", "restConfig"})
public class RabbitMQConfig {

  @Bean
  public Queue roleEnableModifyQueue() {
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
  public DirectExchange rolesExchange() {
    return new DirectExchange("roles-exchange");
  }

  @Bean
  public TopicExchange employeeInfoExchange() {
    return new TopicExchange("employee-info-exchange");
  }

  @Bean
  public FanoutExchange employeeSecExchange() {
    return new FanoutExchange("employee-sec-exchange");
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
  public Binding roleEnableModifyBinding(Queue roleEnableModifyQueue,
                                         Exchange rolesExchange) {
    return BindingBuilder.bind(roleEnableModifyQueue)
        .to(rolesExchange)
        .with("role.enable.modify")
        .noargs();
  }

  @Bean
  public Binding rolesNewBinding(Queue rolesNewQueue, Exchange rolesExchange) {
    return BindingBuilder.bind(rolesNewQueue)
        .to(rolesExchange)
        .with("roles.new")
        .noargs();
  }

  @Bean
  public Binding employeeEmailBinding(Queue employeeEmailQueue,
                                      Exchange employeeInfoExchange) {
    return BindingBuilder.bind(employeeEmailQueue)
        .to(employeeInfoExchange)
        .with("employee.email.*")
        .noargs();
  }
}
