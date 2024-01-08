package com.example.authservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

  private final RabbitTemplate rabbitTemplate;

  public RabbitMQService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String message) {
    rabbitTemplate.convertAndSend("hello-exchange", "hello-key", message);
  }

  public String receiveMessage() {
    return (String)rabbitTemplate.receiveAndConvert("hello-queue");
  }
}
