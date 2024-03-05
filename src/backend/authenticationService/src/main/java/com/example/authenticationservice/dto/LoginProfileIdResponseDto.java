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
public class LoginProfileIdResponseDto {

    @NotBlank(message = "User id cannot be null or blank.")
    @Pattern(regexp = RegexUtils.REGEX_ID_LOGIN_PROFILE,
            message = "User id should start by a capital letter, followed by exactly 5 digits.")
    private String id;

}
