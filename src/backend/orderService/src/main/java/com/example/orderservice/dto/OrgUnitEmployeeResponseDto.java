package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgUnitEmployeeResponseDto {

    private Integer id;
    private String name;
    private AddressResponseDto address;
    private EmployeeCreationRequestDto manager;

}
