package com.example.authenticationservice.utils;

import com.example.authenticationservice.dto.EmployeeResponseAQMPDto;
import com.example.authenticationservice.dto.RoleResponseAQMPDto;
import com.example.authenticationservice.service.EmployeeService;
import com.example.authenticationservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final EmployeeService employeeService;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitListener(queues = "hello-queue")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @RabbitListener(queues = "role-enable-modify-queue")
    public void receiveRoleEnableModifyMessage(String getRoleByNamePath) {
        logger.info("Message received to set a role activation status: " + getRoleByNamePath);
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
            logger.info("Role activation status successfully set");
        } else {
            logger.error("Role activation status set failed");
        }
    }

    @RabbitListener(queues = "roles-init-queue")
    public void receiveRolesInitMessage(String getAllRolesPath) {
        logger.info("Message received on startup of a service to init its roles: " + getAllRolesPath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getAllRolesPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<List<RoleResponseAQMPDto>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
            roleService.saveAllExternalRoles(response.getBody());
            logger.info("Roles successfully initialised");
        } else {
            logger.error("Roles initialisation failed");
        }
    }

    @RabbitListener(queues = "roles-new-queue")
    public void receiveRolesNewMessage(String role) {
        logger.info("Message received to add another service new role: " + role);
        roleService.saveExternalRole(role);
        logger.info("Roles successfully saved");
    }

    @RabbitListener(queues = "employee-email-queue")
    public void receiveEmployeeEmailMessage(String getEmployeeByIdPath) {
        logger.info("Message received to update an employee email: " + getEmployeeByIdPath);
        // build request
        String url = customHttpRequestBuilder.buildUrl(getEmployeeByIdPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
        // send request
        ResponseEntity<EmployeeResponseAQMPDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type
        // update local roles
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
            logger.info("Employee email successfully updated");
            EmployeeResponseAQMPDto employee = response.getBody();
            employeeService.updateEmployeeEmail(employee.getId(), employee.getEmail());
        } else {
            logger.error("Employee email update failed");
        }
    }
}