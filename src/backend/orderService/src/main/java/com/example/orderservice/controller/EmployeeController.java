package com.example.orderservice.controller;

import com.example.orderservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class EmployeeController {

    private final EmployeeService employeeService;

}
