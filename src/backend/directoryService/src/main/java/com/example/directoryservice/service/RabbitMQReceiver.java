package com.example.directoryservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

  @RabbitListener(queues = "employee-sec-exchange")
  public void receiveMessage(String message) {
    System.out.println("Received message about jwt: " + message);
    // Add logic to process the message
  }
}
