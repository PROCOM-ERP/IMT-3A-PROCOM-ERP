package com.example.directoryservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

  private final RabbitTemplate rabbitTemplate;

  public RabbitMQService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String exchange, String routingKey, String message) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

  public String receiveMessage(String queue) {
    return (String)rabbitTemplate.receiveAndConvert(queue);
  }
}
