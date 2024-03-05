package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleActivationResponseDto {

    @NotBlank(message = "Role name cannot be null or blank.")
    private String name;

    @NotBlank(message = "Microservice name cannot be null or blank.")
    private String microservice;

    @NotNull(message = "Role activation status cannot be null.")
    private Boolean isEnable;

}
