package com.example.authservice.service;

import com.example.authservice.dto.EmployeeResponseAQMPEnableDto;
import com.example.authservice.dto.EmployeeRequestDto;
import com.example.authservice.dto.EmployeeResponseDto;
import com.example.authservice.model.Employee;
import com.example.authservice.model.Role;
import com.example.authservice.repository.EmployeeRepository;
import com.example.authservice.utils.RabbitMQSender;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String regexPassword =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*])(?=\\S+$).{12,}$";

    private final EmployeeRepository employeeRepository;
    private final RabbitMQSender rabbitMQSender;
    private final PasswordEncoder passwordEncoder;

    // private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Transactional
    public String createEmployee(EmployeeRequestDto employeeRequestDto) {

        // check password validity
        String password = employeeRequestDto.getPassword();
        checkPasswordValidity(password);

        // create employee
        Integer nextIdEmployee = employeeRepository.getNextIdEmployee();
        Employee employee = Employee.builder()
                .id(generateIdEmployeeFromNextId(nextIdEmployee))
                .idEmployeeGen(nextIdEmployee)
                .email(employeeRequestDto.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(employeeRequestDto.getRoles().stream()
                        .map(roleName -> Role.builder()
                                .name(roleName)
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        // try to save employee with its roles and return its id
        return employeeRepository.save(employee).getId();
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeService::modelToResponseDto)
                .toList();
    }

    public EmployeeResponseDto getEmployee(String idEmployee)
            throws NoSuchElementException {
        return employeeRepository.findById(idEmployee)
                .map(EmployeeService::modelToResponseDto)
                .orElseThrow();
    }

    public EmployeeResponseAQMPEnableDto getEmployeeEnable(String idEmployee)
            throws NoSuchElementException {
        return employeeRepository.findById(idEmployee)
                .map(employee -> EmployeeResponseAQMPEnableDto.builder()
                        .id(employee.getId())
                        .enable(employee.getEnable())
                        .build())
                .orElseThrow();
    }

    public void updateEmployeePassword(String idEmployee, String password)
            throws AccessDeniedException, NoSuchElementException {

        // check if the employee to be modified is the same as the authenticated one
        String currentEmployeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentEmployeeId.equals(idEmployee)) {
            throw new AccessDeniedException("");
        }

        // check password validity
        checkPasswordValidity(password);

        // try to update the password
        int row = employeeRepository.updatePasswordById(idEmployee, passwordEncoder.encode(password));

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

        // send message to other services to update jwt_min_creation for current employee
        rabbitMQSender.sendEmployeeJwtDisableOldMessage(idEmployee);

    }

    public void updateEmployeeRoles(String idEmployee, List<String> roles)
            throws NoSuchElementException {

        // check if employee exists
        Employee employee = employeeRepository.findById(idEmployee).orElseThrow();
        // set roles
        employee.setRoles(roles.stream()
                .map(roleName -> Role.builder()
                        .name(roleName)
                        .build())
                .collect(Collectors.toSet()));
        // save modifications
        employeeRepository.save(employee);

        // send message to other services to update jwt_min_creation for current employee
        rabbitMQSender.sendEmployeeJwtDisableOldMessage(idEmployee);
    }

    public void updateEmployeeEmail(String idEmployee, String email)
            throws NoSuchElementException {

        // try to update the email
        int row =  employeeRepository.updateEmailById(idEmployee, email);

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

        // send message to other services to update jwt_min_creation for current employee
        rabbitMQSender.sendEmployeeEnableModify(idEmployee);
    }

    private String generateIdEmployeeFromNextId(Integer nextIdEmployee) {
        int letterAsciiIndex = 65 + (nextIdEmployee / 100000) % 26;
        int numericPart = nextIdEmployee % 100000;

        return String.format("%c%05d", (char) (letterAsciiIndex), numericPart);
    }

    private void checkPasswordValidity(String password)
            throws IllegalArgumentException {
        if (!password.matches(regexPassword))
            throw new IllegalArgumentException();
    }

    static EmployeeResponseDto modelToResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .email(employee.getEmail())
                .roles(employee.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
