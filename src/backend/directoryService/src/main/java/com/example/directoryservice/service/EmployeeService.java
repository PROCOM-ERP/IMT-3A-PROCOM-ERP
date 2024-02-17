package com.example.directoryservice.service;

import com.example.directoryservice.dto.*;
import com.example.directoryservice.model.Employee;
import com.example.directoryservice.model.Permission;
import com.example.directoryservice.repository.EmployeeRepository;
import com.example.directoryservice.repository.OrgUnitRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrgUnitRepository orgUnitRepository;
    private final AddressService addressService;

    private final MessageSenderService messageSenderService;

    //private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    /* Public Methods */

    public String createEmployee(EmployeeCreationRequestDto employeeDto)
            throws DataIntegrityViolationException {
        // create new entity
        Employee employee = Employee.builder()
                .id(employeeDto.getId())
                .lastName(employeeDto.getLastName())
                .firstName(employeeDto.getFirstName())
                .email(employeeDto.getEmail())
                .phoneNumber(employeeDto.getPhoneNumber())
                .job(employeeDto.getJob())
                .orgUnit(orgUnitRepository.findById(employeeDto.getOrgUnit())
                            .orElseThrow(() -> new DataIntegrityViolationException("")))
                .build();

        // try to save entity and return its id
        return employeeRepository.save(employee).getId();
    }

    public Set<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .collect(Collectors.toSet());
    }

    public EmployeeResponseDto getEmployeeById(String idEmployee) {
        return employeeRepository.findById(idEmployee)
                .map(this::modelToResponseDto)
                .orElseThrow();
    }

    public EmployeeEmailResponseDto getEmployeeEmailById(String idEmployee) {
        return employeeRepository.findById(idEmployee)
                .map(e -> EmployeeEmailResponseDto.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .build())
                .orElseThrow();
    }

    public void updateEmployeeById(String idEmployee, EmployeeUpdateRequestDto employeeDto)
            throws NoSuchElementException, DataIntegrityViolationException {

        // check if the employee to modify is the same as the authenticated one (or admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginProfileId = authentication.getName();
        boolean canBypassAccessDeny = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(Permission.CanBypassAccessDeny.name()));
        if (!currentLoginProfileId.equals(idEmployee) && !canBypassAccessDeny) {
            throw new AccessDeniedException("");
        }

        // check if employee already exists
        Employee employee = employeeRepository.findById(idEmployee).orElseThrow();

        // check if email will change
       boolean isEmailUpdated = ! employeeDto.getEmail().equals(employee.getEmail());

        // update employee attributes
        employee.setLastName(employeeDto.getLastName());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setJob(employeeDto.getJob());
        employee.setOrgUnit(orgUnitRepository.findById(employeeDto.getOrgUnit())
                .orElseThrow(() -> new DataIntegrityViolationException("")));

        // save modifications
        employeeRepository.save(employee);

        // send message to inform the network about employee email update
        if (isEmailUpdated)
            messageSenderService.sendEmployeeEmailUpdateMessage(idEmployee);
    }

    /* Private Methods */

    private EmployeeResponseDto modelToResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .job(employee.getJob())
                .orgUnit(OrgUnitEmployeeResponseDto.builder()
                        .id(employee.getOrgUnit().getId())
                        .name(employee.getOrgUnit().getName())
                        .address(addressService.modelToResponseDto(employee.getOrgUnit().getAddress()))
                        .build())
                .organisation(OrganisationEmployeeResponseDto.builder()
                        .id(employee.getOrgUnit().getOrganisation().getId())
                        .name(employee.getOrgUnit().getOrganisation().getName())
                        .build())
                .build();
    }

}
