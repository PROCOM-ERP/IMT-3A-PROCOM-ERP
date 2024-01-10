package com.example.directoryservice.service;


import com.example.directoryservice.dto.EmployeeRequestDto;
import com.example.directoryservice.dto.EmployeeRequestInfoDto;
import com.example.directoryservice.dto.EmployeeResponseDto;
import com.example.directoryservice.model.Employee;
import com.example.directoryservice.repository.EmployeeRepository;
import com.example.directoryservice.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;

    //private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

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

    public EmployeeResponseDto getEmployee(String idOrEmail) throws NoSuchElementException {
        Optional<Employee> employee = idOrEmail.contains("@") ?
                getEmployeeByEmail(idOrEmail) :
                getEmployeeById(idOrEmail);
        return employee
                .map(EmployeeService::modelToResponseDto)
                .orElseThrow();
    }
    private Optional<Employee> getEmployeeById(String idEmployee) {
        return employeeRepository.findById(idEmployee);

    }
    private Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public void updateEmployeeInfo(String idOrEmail, EmployeeRequestInfoDto employeeRequestInfoDto)
            throws NoSuchElementException {
        // build updated entity
        Employee employee = (idOrEmail.contains("@") ?
                getEmployeeByEmail(idOrEmail) :
                getEmployeeById(idOrEmail))
                .orElseThrow();
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
        int row = idOrEmail.contains("@") ?
                employeeRepository.updateInfoByEmail(employee.getEmail(),  employee.getLastName(),
                        employee.getFirstName(), employee.getPhoneNumber()) :
                employeeRepository.updateInfoById(employee.getId(), employee.getLastName(), employee.getFirstName(),
                        employee.getEmail(), employee.getPhoneNumber());

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateEmployeeService(String idOrEmail, Integer idService)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        int row = idOrEmail.contains("@") ?
                employeeRepository.updateServiceByEmail(idOrEmail, idService) :
                employeeRepository.updateServiceById(idOrEmail, idService);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateAllEmployeesJwtMinCreation() {
        // try to update jwtMinCreation
        employeeRepository.updatAllJwtMinCreation();
    }

    public void updateEmployeeJwtMinCreation(String idEmployee) throws NoSuchElementException {
        // try to update jwtMinCreation
        int row = employeeRepository.updateJwtMinCreationById(idEmployee);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }

    }

    public void updateEmployeeEnable(String idOrEmail, Boolean enable)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update enable
        int row = idOrEmail.contains("@") ?
                employeeRepository.updateEnableByEmail(idOrEmail, enable) :
                employeeRepository.updateEnableById(idOrEmail, enable);

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
