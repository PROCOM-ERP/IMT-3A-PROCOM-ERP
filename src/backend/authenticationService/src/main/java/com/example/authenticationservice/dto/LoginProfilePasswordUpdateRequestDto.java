package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.CustomStringUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginProfilePasswordUpdateRequestDto {

    @NotBlank(message = "User password cannot be null or blank.")
    @Pattern(regexp = CustomStringUtils.REGEX_PASSWORD,
            message = "User password should have at least 12 characters, including 1 capital letter, " +
                    "1 lowercase letter, 1 digit, and 1 special character (@#$%^&+=!.*).")
    private String password;

}
