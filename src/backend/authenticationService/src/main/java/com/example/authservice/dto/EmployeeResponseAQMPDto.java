package com.example.authservice.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeResponseAQMPDto {

    private String id;
    private LocalDate creation;
    private Boolean enable;
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private Integer service;

}
