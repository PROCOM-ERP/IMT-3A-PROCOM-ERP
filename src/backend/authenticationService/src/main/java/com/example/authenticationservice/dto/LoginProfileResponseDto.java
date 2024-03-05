package com.example.authenticationservice.dto;

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
public class LoginProfileResponseDto {

    @NotNull(message = "User activation status cannot be null.")
    private Boolean isEnable;

    @NotNull(message = "User role set cannot be null, but can be empty.")
    private Set<@NotNull(message = "RoleDto cannot be null") RoleDto> roles;

}
