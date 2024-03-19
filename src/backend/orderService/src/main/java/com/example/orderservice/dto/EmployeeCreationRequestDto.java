package com.example.orderservice.dto;

import com.example.orderservice.utils.CustomStringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeCreationRequestDto {

    @NotBlank(message = "User id cannot be null or blank.")
    @Pattern(regexp = CustomStringUtils.REGEX_ID_LOGIN_PROFILE,
            message = "User id should start by a capital letter, followed by exactly 5 digits.")
    private String id;


    @NotBlank(message = "User last name cannot be null or blank.")
    @Size(min = 1, max = 255,
            message = "User last name must contain between 1 and 255 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_EMPLOYEE_LAST_NAME,
            message = "User last name must start with an optional apostrophe, include letters from any language, " +
                    "and may contain spaces, hyphens, periods, or apostrophes between the letters.")
    private String lastName;

    @NotBlank(message = "User first name cannot be null or blank.")
    @Size(min = 1, max = 255,
            message = "User first name must contain between 1 and 255 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_EMPLOYEE_FIRST_NAME,
            message = "User first name must start with an optional apostrophe, include letters from any language, " +
                    "and may contain spaces, hyphens, periods, or apostrophes between the letters.")
    private String firstName;

    @NotBlank(message = "User email cannot be null or blank.")
    @Size(min = 3, max = 320, message = "User email cannot have less than 3 characters and no more than 320.")
    @Email(regexp = CustomStringUtils.REGEX_EMPLOYEE_EMAIL,
            message = "User email should start, and end, with alphanumeric characters, " +
                    "contain a '@' symbol, can contain '-' or dot '.' but not in a row.")
    private String email;

    @NotBlank(message = "User phone number cannot be null or blank.")
    @Size(min = 2, max = 24, message = "User phone number cannot have less than 2 characters and no more than 24.")
    @Pattern(regexp = CustomStringUtils.REGEX_EMPLOYEE_PHONE_NUMBER,
            message = "User phone number must start with an optional plus sign, " +
                    "followed by up to 3 digits for the country code. " +
                    "Phone numbers may include sections of up to 4 digits, separated by spaces or hyphens.")
    private String phoneNumber;

}
