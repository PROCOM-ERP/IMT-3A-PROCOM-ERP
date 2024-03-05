package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCreationRequestDto {

    @NotBlank(message = "Role name cannot be null or blank")
    private String name;

    @NotNull(message = "Role microservice set cannot be null, but can be empty")
    private Set<@NotBlank(message = "Microservice name cannot be null or blank") String> microservices;

}
