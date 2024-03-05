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
public class PermissionDto {

    @NotBlank(message = "Permission name cannot be null or blank.")
    private String name;

    @NotNull(message = "Permission activation status cannot be null.")
    private Boolean isEnable;

}
