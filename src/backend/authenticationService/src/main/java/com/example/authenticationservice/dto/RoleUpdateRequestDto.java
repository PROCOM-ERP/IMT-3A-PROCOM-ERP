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
public class RoleUpdateRequestDto {

    @NotNull(message = "Role activation status cannot be null.")
    private Boolean isEnable;

    @NotNull(message = "Role permission set cannot be null, but can be empty.")
    private Set<@NotBlank(message = "Permission name cannot be null or blank.") String> permissions;

}
