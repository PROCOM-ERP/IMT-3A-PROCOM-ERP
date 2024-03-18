package com.example.directoryservice.controller;

import com.example.directoryservice.dto.EmployeeCreationRequestDto;
import com.example.directoryservice.dto.EmployeeEmailResponseDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.dto.EmployeeUpdateRequestDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(Path.V1_EMPLOYEES)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(operationId = "createEmployee", tags = {"employees"},
            summary = "Create a new employee", description =
            "Create a new employee by providing personal contact information (see body type).<br>" +
            "Information about it are available in URI given in the response header location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Employee created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "OrgUnit : retrieve organisations (organisations section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createEmployee(@Valid @RequestBody EmployeeCreationRequestDto employeeDto) {
        // try to create a new entity
        String idEmployee = employeeService.createEmployee(employeeDto);
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
    @Operation(operationId = "getAllEmployees", tags = {"employees"},
            summary = "Retrieve all employees information", description =
            "Retrieve all employees information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employees information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = EmployeeResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<Set<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok().body(employeeService.getAllEmployees());
    }

    @GetMapping(Path.EMPLOYEE_ID)
    @Operation(operationId = "getEmployeeById", tags = {"employees"},
            summary = "Retrieve one employee information", description =
            "Retrieve one employee information, by providing its id.",
            parameters = {@Parameter(name = "idEmployee", description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employee information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable String idEmployee) {
        return ResponseEntity.ok().body(employeeService.getEmployeeById(idEmployee));
    }

    @GetMapping(Path.EMPLOYEE_ID_EMAIL)
    @Operation(operationId = "getEmployeeEmailById", tags = {"employees"},
            summary = "Retrieve one employee email", description =
            "Retrieve one employee email, by providing its id.",
            parameters = {@Parameter(name = "idEmployee", description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employee email retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeEmailResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<EmployeeEmailResponseDto> getEmployeeEmailById(@PathVariable String idEmployee) {
        return ResponseEntity.ok().body(employeeService.getEmployeeEmailById(idEmployee));
    }

    @PutMapping(Path.EMPLOYEE_ID)
    @Operation(operationId = "updateEmployeeById", tags = {"employees"},
            summary = "Update an employee information", description =
            "Update an employee information, by providing the new one.",
            parameters = {@Parameter(name = "idEmployee", description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee information updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "OrgUnit : retrieve organisations information (organisations section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeById(@PathVariable String idEmployee,
                                                     @Valid @RequestBody EmployeeUpdateRequestDto employeeDto) {
        employeeService.updateEmployeeById(idEmployee, employeeDto);
        return ResponseEntity.noContent().build();
    }
}
