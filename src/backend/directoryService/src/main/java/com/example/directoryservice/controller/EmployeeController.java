package com.example.directoryservice.controller;

import com.example.directoryservice.dto.EmployeeRequestDto;
import com.example.directoryservice.dto.EmployeeRequestInfoDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.EmployeeService;
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
        // try to create a new entity
        String idEmployee = employeeService.createEmployee(employeeRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.EMPLOYEE_ID)
                .buildAndExpand(idEmployee)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok().body(employeeService.getAllEmployees());
    }

    @GetMapping(Path.EMPLOYEE_ID_OR_EMAIL)
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable String idEmployeeOrEmail) {
        if (idEmployeeOrEmail.contains("@"))
            return ResponseEntity.ok().body(employeeService.getEmployeeByEmail(idEmployeeOrEmail));
        return ResponseEntity.ok().body(employeeService.getEmployeeById(idEmployeeOrEmail));
    }

    @PatchMapping(Path.EMPLOYEE_ID_INFO)
    public ResponseEntity<String> updateEmployeeInfo(@PathVariable String idEmployee,
                                                     @RequestBody EmployeeRequestInfoDto employeeRequestInfoDto) {
        employeeService.updateEmployeeInfo(idEmployee, employeeRequestInfoDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_SERVICE)
    public ResponseEntity<String> updateEmployeeService(@PathVariable String idEmployee,
                                                        @RequestBody Integer idService) {
        employeeService.updateEmployeeService(idEmployee, idService);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ENABLE)
    public ResponseEntity<String> updateEmployeeEnable(@PathVariable String idEmployee,
                                                       @RequestBody Boolean enable) {
        employeeService.updateEmployeeEnable(idEmployee, enable);
        return ResponseEntity.noContent().build();
    }

}