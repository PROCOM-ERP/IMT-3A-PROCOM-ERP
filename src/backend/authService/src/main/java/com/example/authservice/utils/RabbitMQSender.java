package com.example.authservice.utils;

import com.example.authservice.model.Path;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    public void sendEmployeesJwtDisableOldMessage() {
        logger.info("Sending message to expire all employee Jwt...");
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employees.jwt.disable.old", "");
        logger.info("Message sent");
    }

    public void sendEmployeeJwtDisableOldMessage(String idEmployee) {
        logger.info("Sending message to expire an employee Jwt...");
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employee.jwt.disable.old", idEmployee);
        logger.info("Message sent");
    }

    public void sendEmployeeEnableModify(String idEmployee) {
        logger.info("Sending message to set an employee activation status...");
        String resource = String.format("%s/%s%s", Path.EMPLOYEES, idEmployee, Path.ENABLE);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employee.enable.modify", path);
        logger.info("Message sent");
    }

}
