package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.RegexUtils;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginProfileCreationRequestDto {

    @NotBlank(message = "User id cannot be null or blank")
    @Size(min = 3, max = 320, message = "User email cannot have less than 3 characters and no more than 320")
    @Email(regexp = RegexUtils.REGEX_EMAIL,
            message = "User email should start, and end, with alphanumeric characters and contain a '@' symbol")
    private String email;

    @NotNull(message = "User role set cannot be null, but can be empty")
    private Set<@NotBlank(message = "User roles cannot be null or blank") String> roles;

}
