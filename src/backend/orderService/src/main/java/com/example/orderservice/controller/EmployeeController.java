package com.example.orderservice.controller;

import com.example.orderservice.dto.EmployeeResponseDto;
import com.example.orderservice.dto.HttpStatusErrorDto;
import com.example.orderservice.model.Path;
import com.example.orderservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Path.V1_EMPLOYEES)
public class EmployeeController {

    /* Service Beans */

    private final EmployeeService employeeService;

    /* Endpoints Methods */

    @GetMapping(Path.EMPLOYEE_ID)
    @Operation(operationId = "getEmployeeAndAddressById", tags = {"employees"},
            summary = "Retrieve one employee information", description =
            "Retrieve one employee information, including its address, by providing its id.",
            parameters = {@Parameter(name = "idEmployee", in = ParameterIn.PATH, description =
                    "The employee username (6 characters identifier)")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Employee information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Employee not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<EmployeeResponseDto> getEmployeeAndAddressById(
            @PathVariable String idEmployee)
    {
        return ResponseEntity.ok(employeeService.getEmployeeAndAddressById(idEmployee));
    }
}
