package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgUnitResponseDto {

    private Integer id;
    private String name;
    //private Set<OrgUnitResponseDto> orgUnits;

}
