package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.CustomStringUtils;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmailResponseDto {

    @NotBlank(message = "User id cannot be null or blank.")
    @Pattern(regexp = CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
            message = "User id should start by a capital letter, followed by exactly 5 digits.")
    private String id;

    @NotBlank(message = "User email cannot be null or blank.")
    @Size(min = 3, max = 320, message = "User email cannot have less than 3 characters and no more than 320.")
    @Email(regexp = CustomStringUtils.REGEX_EMAIL,
            message = "User email should start, and end, with alphanumeric characters, " +
                    "contain a '@' symbol, can contain '-' or dot '.' but not in a row.")
    private String email;

}
