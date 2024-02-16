package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDto {

    private String id;
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String job;
    private OrgUnitEmployeeResponseDto orgUnit;
    private OrganisationEmployeeResponseDto organisation;

}
