package com.example.directoryservice.service;


import com.example.directoryservice.dto.EmployeeRequestDto;
import com.example.directoryservice.dto.EmployeeRequestInfoDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Employee;
import com.example.directoryservice.repository.EmployeeRepository;
import com.example.directoryservice.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;

    public String createEmployee(EmployeeRequestDto employeeRequestDto) throws DataIntegrityViolationException {
        // create new entity
        Employee employee = Employee.builder()
                .id(employeeRequestDto.getId())
                .lastName(employeeRequestDto.getLastName())
                .firstName(employeeRequestDto.getFirstName())
                .email(employeeRequestDto.getEmail())
                .service(serviceRepository.findById(employeeRequestDto.getService())
                        .orElseThrow(() -> new DataIntegrityViolationException("")))
                .build();

        // try to save entity and return its id
        return employeeRepository.save(employee).getId();
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeService::modelToResponseDto)
                .toList();
    }

    public EmployeeResponseDto getEmployeeById(String idEmployee) throws NoSuchElementException {
        return employeeRepository.findById(idEmployee)
                .map(EmployeeService::modelToResponseDto)
                .orElseThrow();
    }

    public EmployeeResponseDto getEmployeeByEmail(String email) throws NoSuchElementException {
        return employeeRepository.findByEmail(email)
                .map(EmployeeService::modelToResponseDto)
                .orElseThrow();
    }

    public void updateEmployeeInfo(String idEmployee, EmployeeRequestInfoDto employeeRequestInfoDto)
            throws NoSuchElementException {
        // build updated entity
        Employee employee = employeeRepository.findById(idEmployee).orElseThrow();
        String lastName = employeeRequestInfoDto.getLastName();
        String firstName = employeeRequestInfoDto.getFirstName();
        String email = employeeRequestInfoDto.getEmail();
        String phoneNumber = employeeRequestInfoDto.getPhoneNumber();
        if (lastName != null && ! lastName.isEmpty())
            employee.setLastName(lastName);
        if (firstName != null && ! firstName.isEmpty())
            employee.setFirstName(firstName);
        if (email != null && ! email.isEmpty())
            employee.setEmail(email);
        if (phoneNumber != null)
            employee.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);

        // try to update information
        int row = employeeRepository.updateInfoById(idEmployee,
                employee.getLastName(),
                employee.getFirstName(),
                employee.getEmail(),
                employee.getPhoneNumber());

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateEmployeeService(String idEmployee, Integer idService)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        int row = employeeRepository.updateServiceById(idEmployee, idService);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateEmployeeEnable(String idEmployee, Boolean enable)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update enable
        int row = employeeRepository.updateEnableById(idEmployee, enable);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    private static EmployeeResponseDto modelToResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .creation(employee.getCreation())
                .enable(employee.getEnable())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .service(employee.getService().getId())
                .build();
    }

}
