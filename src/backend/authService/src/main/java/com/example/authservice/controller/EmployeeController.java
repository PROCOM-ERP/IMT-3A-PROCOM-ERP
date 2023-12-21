package com.example.authservice.controller;

import com.example.authservice.dto.EmployeeRequestDto;
import com.example.authservice.dto.EmployeeResponseDto;
import com.example.authservice.model.Path;
import com.example.authservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(operationId = "createEmployee", tags = {"employees"},
            summary = "Create a new employee", description =
            "Create a new employee by providing roles and plain text password.<br>" +
            "Information about it are available in URI given in the response header location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Employee created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized to create an employee",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        // try to create a new employee
        String idEmployee = employeeService.createEmployee(employeeRequestDto);
        // generate URI location to inform the client how to get information on the new employee
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.EMPLOYEE_ID)
                .buildAndExpand(idEmployee)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();

    }

    @GetMapping
    @Operation(operationId = "getAllEmployees", tags = {"employees"},
            summary = "Retrieve all employees information", description =
            "Retrieve all employees information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employees information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = EmployeeResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized to get employees information",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping(Path.EMPLOYEE_ID)
    @Operation(operationId = "getEmployee", tags = {"employees"},
            summary = "Retrieve one employee information", description =
            "Retrieve one employee information, by providing its id (username).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employee information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized to get employee information",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee information not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
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
