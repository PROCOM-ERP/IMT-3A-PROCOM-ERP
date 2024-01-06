package com.example.directoryservice.controller;

import com.example.directoryservice.dto.EmployeeRequestDto;
import com.example.directoryservice.dto.EmployeeRequestInfoDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Path.V1_EMPLOYEES)
public class EmployeeController {

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(Path.EMPLOYEE_ID)
    public ResponseEntity<EmployeeResponseDto> getEmployee(@PathVariable String idEmployee) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_INFO)
    public ResponseEntity<String> updateEmployeeInfo(@PathVariable String idEmployee,
                                                     @RequestBody EmployeeRequestInfoDto employeeRequestInfoDto) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_SERVICE)
    public ResponseEntity<String> updateEmployeeService(@PathVariable String idEmployee,
                                                        @RequestBody Long idService) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.EMPLOYEE_ID_ENABLE)
    public ResponseEntity<String> updateEmployeeEnable(@PathVariable String idEmployee,
                                                       @RequestBody Boolean enable) {
        // TODO
        return ResponseEntity.ok().build();
    }

}
