package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleActivationResponseDto {

    private String name;
    private String microservice;
    private Boolean isEnable;

}
