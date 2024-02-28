package com.example.orderservice.service;

import com.example.orderservice.dto.AddressCreationRequestDto;
import com.example.orderservice.dto.EmployeeCreationRequestDto;
import com.example.orderservice.model.Employee;
import com.example.orderservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /* Public Methods */
    public Employee createEmployee(EmployeeCreationRequestDto employeeDto)
    {
        // sanitize fields before Employee entity creation
        sanitizeEmployeeCreationRequestDto(employeeDto);

        // retrieve Employee entity if it already exists, else create and save it
        return employeeRepository.findLastCreatedEmployeeMatchingCriteria(
                employeeDto.getId(),
                employeeDto.getLastName(),
                employeeDto.getFirstName(),
                employeeDto.getEmail(),
                employeeDto.getPhoneNumber())
                .orElse(employeeRepository.save(creationRequestDtoToModel(employeeDto)));

    }

    /* Private Methods */
    private void sanitizeEmployeeCreationRequestDto(EmployeeCreationRequestDto employeeDto)
    {
        // sanitize fields before Employee entity creation
        employeeDto.setId(employeeDto.getId().trim());
        employeeDto.setLastName(employeeDto.getLastName().trim());
        employeeDto.setFirstName(employeeDto.getFirstName().trim());
        employeeDto.setEmail(employeeDto.getEmail().trim());
        employeeDto.setPhoneNumber(employeeDto.getPhoneNumber().trim());
    }

    private Employee creationRequestDtoToModel(EmployeeCreationRequestDto employeeDto)
    {
        return Employee.builder()
                .lastName(employeeDto.getLastName())
                .firstName(employeeDto.getFirstName())
                .email(employeeDto.getEmail())
                .phoneNumber(employeeDto.getPhoneNumber())
                .build();
    }

}
