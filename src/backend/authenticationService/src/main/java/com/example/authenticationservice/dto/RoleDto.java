package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleDto {

    @NotBlank(message = "Role name cannot be null or blank.")
    private String name;

    @NotNull(message = "Role activation status cannot be null.")
    private Boolean isEnable;

}
