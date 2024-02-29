package com.example.orderservice.service;

import com.example.orderservice.dto.EmployeeCreationRequestDto;
import com.example.orderservice.dto.EmployeeResponseDto;
import com.example.orderservice.model.Employee;
import com.example.orderservice.model.LoginProfile;
import com.example.orderservice.repository.EmployeeRepository;
import com.example.orderservice.repository.LoginProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.NoSuchElementException;

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
        return employeeRepository.findAllEmployeesById(employeeDto.getId())
                .stream().filter(e -> e.getLastName().equals(employeeDto.getLastName()) &&
                e.getFirstName().equals(employeeDto.getFirstName()) &&
                e.getEmail().equals(employeeDto.getEmail()) &&
                e.getPhoneNumber().equals(employeeDto.getPhoneNumber()))
                .max(Comparator.comparing(Employee::getCreatedAt))
                .orElse(employeeRepository.save(creationRequestDtoToModel(employeeDto, loginProfile)));
    }

    public EmployeeResponseDto getEmployeeById(String idEmployee) throws
            NoSuchElementException
    {
        return employeeRepository.findAllEmployeesById(idEmployee).stream()
                .max(Comparator.comparing(Employee::getCreatedAt))
                .map(this::modelToResponseDto)
                .orElseThrow();
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

    private EmployeeResponseDto modelToResponseDto(Employee employee)
    {
        return EmployeeResponseDto.builder()
                .id(employee.getLoginProfile().getId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .build();
    }

}
