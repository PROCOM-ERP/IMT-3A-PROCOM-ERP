package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolesMicroservicesResponseDto {

    @NotEmpty(message = "Role set cannot be null or empty.")
    private Set<@NotBlank(message = "Role name cannot be null or blank.") String> roles;

    @NotEmpty(message = "Microservice set cannot be null or empty.")
    private Set<@NotBlank(message = "Microservice name cannot be null or blank.") String> microservices;

}
