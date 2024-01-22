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
        info = @Info(
                title = "DirectoryService",
                version = "0.1.0",
                description =
                        "Service to register employees contact information as a directory."),
        tags = {
                @Tag(name = "hello"),
                @Tag(name = "addresses"),
                @Tag(name = "organisations"),
                @Tag(name = "services"),
                @Tag(name = "employees"),
                @Tag(name = "roles"),
                @Tag(name = "permissions")
        })
public class DirectoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DirectoryServiceApplication.class, args);
    }
}
