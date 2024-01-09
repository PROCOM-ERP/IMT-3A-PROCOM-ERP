package com.example.authservice.utils;

import com.example.authservice.dto.RoleEnableModifyAQMPDto;
import com.example.authservice.dto.RoleResponseDto;
import com.example.authservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RabbitMQReceiver {

  private final RoleService roleService;
  private final RestTemplate restTemplate;
  private final CustomHttpRequestBuilder customHttpRequestBuilder;

  @RabbitListener(queues = "hello-queue")
  public void receiveMessage(String message) {
    System.out.println("Received message : " + message);
  }

  @RabbitListener(queues = "role-enable-modify-queue")
  public void receiveRoleEnableModifyMessage(RoleEnableModifyAQMPDto role) {
    System.out.println("Received message of a role that needs to be enabled / disabled : " + role);
    roleService.updateRoleEnableCounter(role.getName(), role.getEnable(), false);
  }

  @RabbitListener(queues = "roles-init-queue")
  public void receiveRoleInitMessage(String path) {
    System.out.println("Received message on startup of service : " + path);
    String url = customHttpRequestBuilder.buildUrl(path);
    HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
    ResponseEntity<List<RoleResponseDto>> response = restTemplate.exchange(url, HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {});

  }

  @RabbitListener(queues = "roles-new-queue")
  public void receiveRolesNewMessage(String message) {
    System.out.println("Received message of a new role in an up and running service : " + message);
    // TODO
  }

  @RabbitListener(queues = "employee-email-queue")
  public void receiveEmployeeEmailMessage(String message) {
    System.out.println("Received message of an update in employee information : " + message);
    // TODO
  }
}
