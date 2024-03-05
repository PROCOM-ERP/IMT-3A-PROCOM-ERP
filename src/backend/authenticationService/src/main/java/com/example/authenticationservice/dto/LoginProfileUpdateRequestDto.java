package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginProfileUpdateRequestDto {

    @NotNull(message = "User activation status cannot be null")
    private Boolean isEnable;

    @NotNull(message = "User role set cannot be null, but can be empty")
    private Set<@NotBlank(message = "User roles cannot be null or blank") String> roles;

}
