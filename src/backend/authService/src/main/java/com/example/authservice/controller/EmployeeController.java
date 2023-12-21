package com.example.authservice.controller;

import com.example.authservice.dto.EmployeeRequestDto;
import com.example.authservice.dto.EmployeeResponseDto;
import com.example.authservice.model.Path;
import com.example.authservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Path.V1_EMPLOYEES)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        String idEmployee = employeeService.createEmployee(employeeRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.EMPLOYEE_ID)
                .buildAndExpand(idEmployee)
                .toUri();
        return ResponseEntity.created(location).build();

    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping(Path.EMPLOYEE_ID)
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable String idEmployee) {
        return ResponseEntity.ok(employeeService.getEmployee(idEmployee));
    }

    @PatchMapping(Path.EMPLOYEE_ID_PASSWORD)
    public ResponseEntity<String> updateEmployeePassword(@PathVariable String idEmployee,
                                                         @RequestBody String password) {
        employeeService.updateEmployeePassword(idEmployee, password);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ROLES)
    public ResponseEntity<String> updateEmployeeRoles(@PathVariable String idEmployee,
                                                      @RequestBody List<String> roles) {
        employeeService.updateEmployeeRoles(idEmployee, roles);
        return ResponseEntity.noContent().build();
    }

}
