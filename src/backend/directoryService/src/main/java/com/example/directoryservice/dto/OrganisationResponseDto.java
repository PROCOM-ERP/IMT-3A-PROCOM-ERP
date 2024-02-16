package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationResponseDto {

    private Integer id;
    private String name;
    private Set<OrgUnitResponseDto> orgUnits;

}
