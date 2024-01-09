package com.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

  private final RabbitTemplate rabbitTemplate;

  public void sendMessage(String exchange, String routingKey, String message) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

  public String receiveMessage(String queue) {
    return (String)rabbitTemplate.receiveAndConvert(queue);
  }
}
