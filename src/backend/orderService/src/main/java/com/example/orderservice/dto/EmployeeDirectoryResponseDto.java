package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmployeeDirectoryResponseDto {

    private String id;
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String job;
    private OrgUnitEmployeeResponseDto orgUnit;
    private OrganisationEmployeeResponseDto organisation;

}
