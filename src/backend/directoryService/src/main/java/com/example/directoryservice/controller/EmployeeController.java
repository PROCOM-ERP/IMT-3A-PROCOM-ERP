package com.example.directoryservice.controller;

import com.example.directoryservice.dto.EmployeeRequestDto;
import com.example.directoryservice.dto.EmployeeRequestInfoDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.EmployeeService;
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
                    "Service : retrieve services information (services section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        // try to create a new entity
        String idEmployee = employeeService.createEmployee(employeeRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.EMPLOYEE_ID_OR_EMAIL)
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
        return ResponseEntity.ok().body(employeeService.getAllEmployees());
    }

    @GetMapping(Path.EMPLOYEE_ID_OR_EMAIL)
    @Operation(operationId = "getEmployee", tags = {"employees"},
            summary = "Retrieve one employee information", description =
            "Retrieve one employee information, by providing its id or email.",
            parameters = {@Parameter(name = "idOrEmail", description =
                    "The employee username (6 characters identifier) or email address")})
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
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable String idOrEmail) {
        return ResponseEntity.ok().body(employeeService.getEmployee(idOrEmail));
    }

    @PatchMapping(Path.EMPLOYEE_ID_OR_EMAIL_INFO)
    @Operation(operationId = "updateEmployeeInfo", tags = {"employees"},
            summary = "Update an employee information", description =
            "Update an employee last name, first name, email and / or phone number, by providing the new ones.",
            parameters = {@Parameter(name = "idOrEmail", description =
                    "The employee username (6 characters identifier) or email address")})
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
                    "Email : 63 characters for 'username', @, 63 for subdomain, and the rest for TLD. Maximum 320 characters.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeInfo(@PathVariable String idOrEmail,
                                                     @RequestBody EmployeeRequestInfoDto employeeRequestInfoDto) {
        employeeService.updateEmployeeInfo(idOrEmail, employeeRequestInfoDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_OR_EMAIL_SERVICE)
    @Operation(operationId = "updateEmployeeService", tags = {"employees"},
            summary = "Update an employee service", description =
            "Update an employee service, by providing the new one.",
            parameters = {@Parameter(name = "idOrEmail", description =
                    "The employee username (6 characters identifier) or email address")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Employee service updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "Service : retrieve services information (services section) to know which one are available.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateEmployeeService(@PathVariable String idOrEmail,
                                                        @RequestBody Integer idService) {
        employeeService.updateEmployeeService(idOrEmail, idService);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_OR_EMAIL_ENABLE)
    @Operation(operationId = "updateEmployeeEnable", tags = {"employees"},
            summary = "Enable or disable an employee", description =
            "Enable or disable an employee, by providing a new enable value (true or false).<br>" +
            "Only available for admins.",
            parameters = {@Parameter(name = "idOrEmail", description =
                    "The employee username (6 characters identifier) or email address")})
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
    public ResponseEntity<String> updateEmployeeEnable(@PathVariable String idOrEmail,
                                                       @RequestBody Boolean enable) {
        employeeService.updateEmployeeEnable(idOrEmail, enable);
        return ResponseEntity.noContent().build();
    }

}
