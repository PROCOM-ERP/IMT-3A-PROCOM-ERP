package com.example.authenticationservice.utils;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RegexUtils {

    public static final String REGEX_ID_LOGIN_PROFILE =
            "^[A-Z][0-9]{5}$";
    public static final String REGEX_EMAIL =
            "^[a-z0-9]([\\-\\.]?[a-z0-9])*@[a-z0-9]([\\-\\.]?[a-z0-9])*$";
    public static final String REGEX_PASSWORD =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*])(?=\\S+$).{12,}$";

    public void checkStringPattern(String input, String regex, String message) {
        if (input == null || ! input.matches(regex))
            throw new ValidationException(message);
    }

    public void checkNullOrBlankString(String input, String message) {
        if (input == null || input.isBlank())
            throw new ValidationException(message);
    }
}
