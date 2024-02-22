package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUpdateRequestDto {

    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String job;
    private Integer orgUnit;

}
