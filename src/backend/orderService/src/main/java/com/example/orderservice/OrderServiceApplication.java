package com.example.orderservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
@OpenAPIDefinition(
        info = @Info(
                title = "OrderService",
                version = "1.0.0",
                description =
                        "Service to manage orders for the company."),
        tags = {
                @Tag(name = "hello"),
                @Tag(name = "employees"),
                @Tag(name = "orders"),
                @Tag(name = "providers"),
                @Tag(name = "roles")
        })
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
