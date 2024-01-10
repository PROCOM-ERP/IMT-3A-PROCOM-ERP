package com.example.directoryservice.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender implements CommandLineRunner {

  private final RabbitTemplate rabbitTemplate;

  public RabbitMQSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Sending message to auth service about roles...");
    rabbitTemplate.convertAndSend(
        "roles-exchange", "roles.init",
        "Hello from DirectoryService test message in init roles");
  }
}
