package com.example.authservice.utils;

import com.example.authservice.model.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    private final FanoutExchange fanoutExchange;

    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    public void sendEmployeesJwtDisableOldMessage() {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employees.jwt.disable.old", "");
    }

    public void sendEmployeeJwtDisableOldMessage(String idEmployee) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employee.jwt.disable.old", idEmployee);
    }

    public void sendEmployeeEnableModify(String idEmployee) {
        String resource = String.format("%s/%s%s", Path.EMPLOYEES, idEmployee, Path.ENABLE);
        String path = customHttpRequestBuilder.buildPath(Path.V1, resource);
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "employee.enable.modify", path);
    }

}
