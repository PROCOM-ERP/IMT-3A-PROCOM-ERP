package com.example.authservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

  @RabbitListener(queues = "hello-queue")
  public void receiveMessage(String message) {
    System.out.println("Received message: " + message);
    // Add logic to process the message
  }

  @RabbitListener(queues = "role-enable-modify-queue")
  public void receiveRoleEnableModifyMessage(String message) {
    System.out.println(
        "Received message of a role that needs to be enabled / disabled : " +
        message);
    // Add logic to process the message
  }

  @RabbitListener(queues = "roles-init-queue")
  public void receiveRoleInitMessage(String message) {
    System.out.println("Received message on startup of service : " + message);
    // Add logic to process the message
  }

  @RabbitListener(queues = "roles-new-queue")
  public void receiveRolesNewMessage(String message) {
    System.out.println(
        "Received message of a new role in an up and running service : " +
        message);
    // Add logic to process the message
  }

  @RabbitListener(queues = "employee-email-queue")
  public void receiveEmployeeInfoMessage(String message) {
    System.out.println(
        "Received message of an update in employee information : " + message);
    // Add logic to process the message
  }
}
