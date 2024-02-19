package com.example.inventoryservice;

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
                title = "InventoryService",
                version = "0.1.0",
                description =
                        "Service that offers the possibility of managing products data in the company." +
                                "This service expose the data of the products, categories and address." +
                                "The user can also add, modify, update and remove data."),
        tags = {
                @Tag(name = "hello"),
                @Tag(name = "addresses"),
                @Tag(name = "categories"),
                @Tag(name = "products")
        })
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
