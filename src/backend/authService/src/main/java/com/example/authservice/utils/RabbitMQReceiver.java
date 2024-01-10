package com.example.authservice.utils;

import com.example.authservice.dto.EmployeeResponseAQMPDto;
import com.example.authservice.dto.RoleResponseAQMPDto;
import com.example.authservice.dto.RoleResponseDto;
import com.example.authservice.service.EmployeeService;
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
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RabbitMQReceiver {

    private final RoleService roleService;
    private final EmployeeService employeeService;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    @RabbitListener(queues = "hello-queue")
    public void receiveMessage(String message) {
        System.out.println("Received message : " + message);
    }

    @RabbitListener(queues = "role-enable-modify-queue")
    public void receiveRoleEnableModifyMessage(String getRoleByNamePath) {
        System.out.println("Received message of a role that needs to be enabled / disabled : " + getRoleByNamePath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getRoleByNamePath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<RoleResponseAQMPDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type
        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
            RoleResponseAQMPDto role = response.getBody();
            roleService.updateRoleEnableCounter(role.getName(), role.getEnable());
        }
    }

    @RabbitListener(queues = "roles-init-queue")
    public void receiveRolesInitMessage(String getAllRolesPath) {
        System.out.println("Received message on startup of service : " + getAllRolesPath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getAllRolesPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<List<RoleResponseAQMPDto>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type
        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null)
            roleService.saveAllExternalRoles(response.getBody());
    }

    @RabbitListener(queues = "roles-new-queue")
    public void receiveRolesNewMessage(String role) {
        System.out.println("Received message of a new role in an up and running service : " + role);
        roleService.saveNewExternalRole(role);
    }

    @RabbitListener(queues = "employee-email-queue")
    public void receiveEmployeeEmailMessage(String getEmployeeByIdPath) {
        System.out.println("Received message of an update in employee information : " + getEmployeeByIdPath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getEmployeeByIdPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<EmployeeResponseAQMPDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type
        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
            EmployeeResponseAQMPDto employee = response.getBody();
            employeeService.updateEmployeeEmail(employee.getId(), employee.getEmail());
        }
    }
}
