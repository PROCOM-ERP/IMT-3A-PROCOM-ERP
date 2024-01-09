package com.example.directoryservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
@OpenAPIDefinition(
    info = @Info(title = "DirectoryService", version = "0.0.1-SNAPSHOT",
                 description = ""),
    tags =
    { @Tag(name = "hello")
      , @Tag(name = "roles"), @Tag(name = "permissions") })
public class DirectoryServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(DirectoryServiceApplication.class, args);
  }
}
