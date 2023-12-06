package com.example.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping(produces = "application/json")
    @Operation(
            summary = "GET Hello World !",
            description = "GET Hello World ! from AuthService",
            operationId = "getHello",
            tags = {"hello"}
    )
    public @ResponseBody ResponseEntity<String> getHello() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Hello, World !");
    }

}
