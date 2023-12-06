package com.example.authservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
            title = "AuthService",
            description =
                    "Service to register users with password and roles.\n" +
                    "It allows to get JWT token for future connections to other services, by logging with BasicAuth.",
            version = "0.0.1-SNAPSHOT"
        ),
        tags = {
            @Tag(name = "hello"),
            @Tag(name = "users"),
            @Tag(name = "login")
        }
)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
