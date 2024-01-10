package com.example.directoryservice.utils;

import com.example.directoryservice.dto.EmployeeResponseAQMPEnableDto;
import com.example.directoryservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RabbitMQReceiver {

    private final EmployeeService employeeService;

    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    private final RestTemplate restTemplate;

    @RabbitListener(queues = "employee-sec-queue")
    public void receiveEmployeeSecMessage(String message, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        System.out.println("Received message about jwt: " + message);
        switch (routingKey) {
            case "employees.jwt.disable.old":
                employeeService.updateAllEmployeesJwtMinCreation();
                break;
            case "employee.jwt.disable.old":
                employeeService.updateEmployeeJwtMinCreation(message);
                break;
            case "employee.enable.modify":
                // build request
                String url = customHttpRequestBuilder.buildUrl(message); // message contains the path
                HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();
                // send request
                ResponseEntity<EmployeeResponseAQMPEnableDto> response = restTemplate.exchange(url, HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}); // response with custom type
                // update local roles
                if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
                    EmployeeResponseAQMPEnableDto employee = response.getBody();
                    employeeService.updateEmployeeEnable(employee.getId(), employee.getEnable());
                }
                break;
            default:
        }
    }
}
