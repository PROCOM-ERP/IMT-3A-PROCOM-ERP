package com.example.directoryservice.service;

import com.example.directoryservice.annotation.LogExecutionTime;
import com.example.directoryservice.dto.*;
import com.example.directoryservice.model.Employee;
import com.example.directoryservice.model.Permission;
import com.example.directoryservice.repository.EmployeeRepository;
import com.example.directoryservice.repository.OrgUnitRepository;
import com.example.directoryservice.utils.CustomLogger;
import com.example.directoryservice.utils.CustomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    /* Constants */
    public static final String ERROR_MSG_EMPLOYEE_ID_BLANK =
            "User id cannot be null or blank.";
    public static final String ERROR_MSG_EMPLOYEE_ID_SIZE =
            "User id must contain exactly 6 characters.";
    public static final String ERROR_MSG_EMPLOYEE_ID_PATTERN =
            "User id should start by a capital letter, followed by exactly 5 digits.";

    /* Repository Beans */
    private final EmployeeRepository employeeRepository;
    private final OrgUnitRepository orgUnitRepository;

    /* Service Beans */
    private final AddressService addressService;
    private final MessageSenderService messageSenderService;

    /* Utils Beans */
    private final CustomStringUtils customStringUtils;

    /* Public Methods */

    @Transactional
    @LogExecutionTime(description = "Create new user information profile.",
            tag = CustomLogger.TAG_USERS)
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
        String idEmployee = employeeRepository.save(employee).getId();

        // send message to inform the network about employee email update
        messageSenderService.sendEmployeeEmailUpdateMessage(idEmployee);

        return idEmployee;
    }

    @LogExecutionTime(description = "Retrieve all user information profiles.",
            tag = CustomLogger.TAG_USERS)
    public Set<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .collect(Collectors.toSet());
    }

    @LogExecutionTime(description = "Retrieve a user information profile.",
            tag = CustomLogger.TAG_USERS)
    public EmployeeResponseDto getEmployeeById(String idEmployee)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check employee id value
        customStringUtils.checkNullOrBlankString(idEmployee, ERROR_MSG_EMPLOYEE_ID_BLANK);
        customStringUtils.checkStringSize(idEmployee, ERROR_MSG_EMPLOYEE_ID_SIZE, 6, 6);
        customStringUtils.checkStringPattern(idEmployee, CustomStringUtils.REGEX_ID_LOGIN_PROFILE, ERROR_MSG_EMPLOYEE_ID_PATTERN);

        // retrieve and return Employee entity
        return employeeRepository.findById(idEmployee)
                .map(this::modelToResponseDto)
                .orElseThrow();
    }

    @LogExecutionTime(description = "Retrieve a user email.",
            tag = CustomLogger.TAG_USERS)
    public EmployeeEmailResponseDto getEmployeeEmailById(String idEmployee) {

        // check employee id value
        customStringUtils.checkNullOrBlankString(idEmployee, ERROR_MSG_EMPLOYEE_ID_BLANK);
        customStringUtils.checkStringSize(idEmployee, ERROR_MSG_EMPLOYEE_ID_SIZE, 6, 6);
        customStringUtils.checkStringPattern(idEmployee, CustomStringUtils.REGEX_ID_LOGIN_PROFILE, ERROR_MSG_EMPLOYEE_ID_PATTERN);

        // retrieve and return EmployeeEmailResponseDto entity
        return employeeRepository.findById(idEmployee)
                .map(e -> EmployeeEmailResponseDto.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .build())
                .orElseThrow();
    }

    @LogExecutionTime(description = "Update a user information.",
            tag = CustomLogger.TAG_USERS)
    public void updateEmployeeById(
            String idEmployee,
            EmployeeUpdateRequestDto employeeDto)
            throws NoSuchElementException,
            DataIntegrityViolationException
    {
        // check employee id value
        customStringUtils.checkNullOrBlankString(idEmployee, ERROR_MSG_EMPLOYEE_ID_BLANK);
        customStringUtils.checkStringSize(idEmployee, ERROR_MSG_EMPLOYEE_ID_SIZE, 6, 6);
        customStringUtils.checkStringPattern(idEmployee, CustomStringUtils.REGEX_ID_LOGIN_PROFILE, ERROR_MSG_EMPLOYEE_ID_PATTERN);

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
                        .manager(modelToManagerResponseDto(employee.getOrgUnit().getManager()))
                        .build())
                .organisation(OrganisationEmployeeResponseDto.builder()
                        .id(employee.getOrgUnit().getOrganisation().getId())
                        .name(employee.getOrgUnit().getOrganisation().getName())
                        .build())
                .build();
    }

    private ManagerResponseDto modelToManagerResponseDto(Employee employee)
    {
        return ManagerResponseDto.builder()
                .id(employee.getId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .build();
    }

}
