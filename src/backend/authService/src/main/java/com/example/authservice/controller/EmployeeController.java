package com.example.authservice.controller;

import com.example.authservice.dto.EmployeeResponseAQMPEnableDto;
import com.example.authservice.dto.EmployeeRequestDto;
import com.example.authservice.dto.EmployeeResponseDto;
import com.example.authservice.model.Path;
import com.example.authservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
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
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
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
            "Retrieve one employee information, by providing its id (username).",
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
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable String idEmployee) {
        return ResponseEntity.ok(employeeService.getEmployee(idEmployee));
    }

    @GetMapping(Path.EMPLOYEE_ID_ENABLE)
    @Operation(operationId = "getEmployeeEnable", tags = {"employees"},
            summary = "Retrieve one employee information about activation status", description =
            "Retrieve one employee information about activation status, by providing its id (username).",
            parameters = {@Parameter(name = "idEmployee", description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employee activation status retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeResponseAQMPEnableDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<EmployeeResponseAQMPEnableDto> getEmployeeEnable(@PathVariable String idEmployee) {
        return ResponseEntity.ok(employeeService.getEmployeeEnable(idEmployee));
    }

    @PatchMapping(Path.EMPLOYEE_ID_PASSWORD)
    @Operation(operationId = "updateEmployeePassword", tags = {"employees"},
            summary = "Update an employee password", description =
            "Update an employee password. Only available for the employee itself.",
            parameters = {@Parameter(name = "idEmployee", description =
            "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee password updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "403", description =
                    "Authenticated employee cannot update an other employee password",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Password : 12 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character in (@#$%^&+=!.*).<br>",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeePassword(@PathVariable String idEmployee,
                                                         @RequestBody String password) {
        employeeService.updateEmployeePassword(idEmployee, password);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ROLES)
    @Operation(operationId = "updateEmployeeRoles", tags = {"employees"},
            summary = "Update an employee roles", description =
            "Update an employee roles, by providing a list of all the new ones.<br>" +
            "Previous ones will be deleted.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idEmployee", description =
            "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee roles updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Roles : retrieve roles information (roles section) to know which one are available",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeRoles(@PathVariable String idEmployee,
                                                      @RequestBody List<String> roles) {
        employeeService.updateEmployeeRoles(idEmployee, roles);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_EMAIL)
    @Operation(operationId = "updateEmployeeEmail", tags = {"employees"},
            summary = "Update an employee email", description =
            "Update an employee email, by providing the new one.<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idEmployee", description =
            "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee email updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Email : 63 characters for 'username', @, 63 for subdomain, and the rest for TLD. Maximum 320 characters.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeEmail(@PathVariable String idEmployee,
                                                       @RequestBody String email) {
        employeeService.updateEmployeeEmail(idEmployee, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ENABLE)
    @Operation(operationId = "updateEmployeeEmail", tags = {"employees"},
            summary = "Enable or disable an employee", description =
            "Enable or disable an employee, by providing a new enable value (true or false).<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idEmployee", description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee enable attribute updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Enable : boolean value (true or false).",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeEnable(@PathVariable String idEmployee,
                                                       @RequestBody Boolean enable) {
        employeeService.updateEmployeeEnable(idEmployee, enable);
        return ResponseEntity.noContent().build();
    }

}
