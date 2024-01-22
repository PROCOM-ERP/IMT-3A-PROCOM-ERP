package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDto {

    private String id;
    private LocalDate creation;
    private Boolean enable;
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private Integer service;

}
