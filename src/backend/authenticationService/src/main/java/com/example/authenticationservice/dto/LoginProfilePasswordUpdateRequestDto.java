package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.RegexUtils;
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

    @NotBlank(message = "User password cannot be null or blank")
    @Pattern(regexp = RegexUtils.REGEX_PASSWORD,
            message = "User password should have at least 12 characters, including 1 capital letter, " +
                    "1 lowercase letter, 1 digit, 1 spacial character (@#$%^&+=!.*) and ")
    private String password;

}
