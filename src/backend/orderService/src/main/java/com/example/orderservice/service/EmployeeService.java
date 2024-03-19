package com.example.orderservice.service;

import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.dto.AddressResponseDto;
import com.example.orderservice.dto.EmployeeCreationRequestDto;
import com.example.orderservice.dto.EmployeeDirectoryResponseDto;
import com.example.orderservice.dto.EmployeeResponseDto;
import com.example.orderservice.model.Address;
import com.example.orderservice.model.Employee;
import com.example.orderservice.model.LoginProfile;
import com.example.orderservice.repository.EmployeeRepository;
import com.example.orderservice.repository.LoginProfileRepository;
import com.example.orderservice.utils.CustomHttpRequestBuilder;
import com.example.orderservice.utils.CustomLogger;
import com.example.orderservice.utils.CustomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

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
    private final LoginProfileRepository loginProfileRepository;

    /* Utils Beans */

    private final CustomHttpRequestBuilder customHttpRequestBuilder;
    private final CustomStringUtils customStringUtils;
    private final RestTemplate restTemplate;

    /* Public Methods */
    @Transactional
    @LogExecutionTime(description = "Create new user information profile.",
            tag = CustomLogger.TAG_USERS)
    public Employee createEmployee(EmployeeCreationRequestDto employeeDto)
            throws DataIntegrityViolationException
    {
        // check if a LoginProfile exists for this Employee and retrieve it
        LoginProfile loginProfile = loginProfileRepository.findById(employeeDto.getId())
                .orElseThrow(() -> new DataIntegrityViolationException("Non-existing LoginProfile for this id"));

        // retrieve Employee entity if it already exists, else create and save it
        return employeeRepository.findAllEmployeesById(employeeDto.getId())
                .stream().filter(e -> e.getLastName().equals(employeeDto.getLastName()) &&
                e.getFirstName().equals(employeeDto.getFirstName()) &&
                e.getEmail().equals(employeeDto.getEmail()) &&
                (e.getPhoneNumber() == null || e.getPhoneNumber().equals(employeeDto.getPhoneNumber())))
                .max(Comparator.comparing(Employee::getCreatedAt))
                .orElse(employeeRepository.save(creationRequestDtoToModel(employeeDto, loginProfile)));
    }

    @LogExecutionTime(description = "Retrieve a user information profile with location.",
            tag = CustomLogger.TAG_USERS)
    public EmployeeResponseDto getEmployeeAndAddressById(String idEmployee)
            throws IllegalArgumentException,
            NoSuchElementException
    {
        // check employee id value
        customStringUtils.checkNullOrBlankString(idEmployee, ERROR_MSG_EMPLOYEE_ID_BLANK);
        customStringUtils.checkStringSize(idEmployee, ERROR_MSG_EMPLOYEE_ID_SIZE, 6, 6);
        customStringUtils.checkStringPattern(idEmployee, CustomStringUtils.REGEX_ID_LOGIN_PROFILE, ERROR_MSG_EMPLOYEE_ID_PATTERN);

        List<Object[]> results = employeeRepository.findEmployeeAndLastOrderAddressByIdLoginProfile(
                idEmployee, PageRequest.of(0, 1));
        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            Employee employee = (Employee) result[0];
            Address address = (Address) result[1];
            return employeeAndAddressToEmployeeResponseDto(employee, address);
        } else {
            throw new NoSuchElementException("No employee and address information found in orders for this ID");
        }
    }

    @LogExecutionTime(description = "Retrieve a user information with its manager.",
            tag = CustomLogger.TAG_ORDERS)
    public EmployeeDirectoryResponseDto getEmployeeFromMicroserviceById(String getEmployeeByIdPath)
    {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getEmployeeByIdPath);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<EmployeeDirectoryResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new RestClientException("");

        // return expected LoginProfile activation
        return response.getBody();
    }

    /* Private Methods */
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

    private EmployeeResponseDto employeeAndAddressToEmployeeResponseDto(Employee employee, Address address) {
        return EmployeeResponseDto.builder()
                .id(employee.getLoginProfile().getId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .address(AddressResponseDto.builder()
                        .number(address.getNumber())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .state(address.getState())
                        .country(address.getCountry())
                        .zipcode(address.getZipcode())
                        .info(address.getInfo())
                        .build())
                .build();
    }

}
