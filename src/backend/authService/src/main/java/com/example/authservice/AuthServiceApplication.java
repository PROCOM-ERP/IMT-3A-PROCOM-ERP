package com.example.authservice;

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
        title = "AuthService", version = "0.0.1-SNAPSHOT",
        description =
            "Service to register employees with password and roles.<br>"
            +
            "It allows to get JWT token for future connections to other services, by logging with BasicAuth.")
    ,
    tags =
    {
      @Tag(name = "hello")
      , @Tag(name = "rabbithello"), @Tag(name = "auth"),
          @Tag(name = "employees"), @Tag(name = "roles"),
          @Tag(name = "permissions")
    })
public class AuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
