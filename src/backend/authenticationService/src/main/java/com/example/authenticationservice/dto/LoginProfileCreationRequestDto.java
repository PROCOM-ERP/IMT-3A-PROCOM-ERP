package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.CustomStringUtils;
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

    @NotBlank(message = "User email cannot be null or blank.")
    @Size(min = 3, max = 320, message = "User email cannot have less than 3 characters and no more than 320.")
    @Email(regexp = CustomStringUtils.REGEX_EMAIL,
            message = "User email should start, and end, with alphanumeric characters, " +
                    "contain a '@' symbol, can contain '-' or dot '.' but not in a row.")
    private String email;

    @NotNull(message = "User role set cannot be null, but can be empty.")
    private Set<
            @NotBlank(message = "Role cannot be null or blank.")
            @Size(min = 1, max = 32, message = "Role name must contain between 1 and 32 characters.")
            @Pattern(regexp = CustomStringUtils.REGEX_ROLE_NAME,
                    message = "Role name must start with a letter and can only contain letters, numbers, dashes, and dots. " +
                            "Consecutive special characters are not allowed.")
            String> roles;

}
