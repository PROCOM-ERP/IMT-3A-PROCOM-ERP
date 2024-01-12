package com.example.directoryservice.utils;

import com.example.directoryservice.dto.EmployeeResponseAQMPEnableDto;
import com.example.directoryservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitListener(queues = "employee-sec-queue")
    public void receiveEmployeeSecMessage(String message, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        switch (routingKey) {
            case "employees.jwt.disable.old":
                logger.info("Message received to expire all employee Jwt: " + message);
                employeeService.updateAllEmployeesJwtMinCreation();
                logger.info("Jwt min creation successfully updated for all employees");
                break;
            case "employee.jwt.disable.old":
                logger.info("Message received to expire an employee Jwt: " + message);
                employeeService.updateEmployeeJwtMinCreation(message);
                logger.info("Jwt min creation successfully updated for the employee");
                break;
            case "employee.enable.modify":
                logger.info("Message received to set an employee activation status: " + message);
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
                    logger.info("Employee activation status successfully updated");
                } else {
                    logger.error("Employee activation status update failed");
                }
                break;
            default:
        }
    }
}
