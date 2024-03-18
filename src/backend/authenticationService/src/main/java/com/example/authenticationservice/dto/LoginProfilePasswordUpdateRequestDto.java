package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.CustomStringUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginProfilePasswordUpdateRequestDto {

    @NotBlank(message = "User password cannot be null or blank.")
    @Size(min = 12, message = "User password must contain at least 12 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_PASSWORD,
            message = "User password should have at least 12 characters, including 1 capital letter, " +
                    "1 lowercase letter, 1 digit, and 1 special character (@#$%^&+=!.*).")
    private String password;

}
