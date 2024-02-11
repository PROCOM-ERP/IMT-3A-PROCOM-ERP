package com.example.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleActivationDto {

    private String name;
    private String microservice;
    private Boolean isEnable;

}