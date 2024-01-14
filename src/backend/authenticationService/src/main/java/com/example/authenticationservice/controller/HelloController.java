package com.example.authenticationservice.controller;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<String> getHello() {
        return ResponseEntity.ok("Hello, World !");
    }
}

