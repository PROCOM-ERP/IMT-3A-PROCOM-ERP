package com.example.orderservice.service;

import com.example.orderservice.dto.EmployeeCreationRequestDto;
import com.example.orderservice.model.Employee;
import com.example.orderservice.model.LoginProfile;
import com.example.orderservice.repository.EmployeeRepository;
import com.example.orderservice.repository.LoginProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final LoginProfileRepository loginProfileRepository;

    /* Public Methods */
    public Employee createEmployee(EmployeeCreationRequestDto employeeDto)
    {
        // sanitize fields before Employee entity creation
        sanitizeEmployeeCreationRequestDto(employeeDto);

        // check if a LoginProfile exists for this Employee and retrieve it
        LoginProfile loginProfile = loginProfileRepository.findById(employeeDto.getId())
                .orElseThrow(() -> new DataIntegrityViolationException("Non-existing LoginProfile for this id"));

        // retrieve Employee entity if it already exists, else create and save it
        return employeeRepository.findLastCreatedEmployeeMatchingCriteria(
                employeeDto.getId(),
                employeeDto.getLastName(),
                employeeDto.getFirstName(),
                employeeDto.getEmail(),
                employeeDto.getPhoneNumber())
                .stream()
                .max(Comparator.comparing(Employee::getCreatedAt))
                .orElse(employeeRepository.save(creationRequestDtoToModel(employeeDto, loginProfile)));

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

    private Employee creationRequestDtoToModel(EmployeeCreationRequestDto employeeDto, LoginProfile loginProfile)
    {
        return Employee.builder()
                .lastName(employeeDto.getLastName())
                .firstName(employeeDto.getFirstName())
                .email(employeeDto.getEmail())
                .phoneNumber(employeeDto.getPhoneNumber())
                .loginProfile(loginProfile)
                .build();
    }

}
