package com.example.authenticationservice.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CustomPasswordGenerator {

    private final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String DIGITS = "0123456789";
    private final String SPECIAL_CHARACTERS = "@#$%^&+=!*";
    private final String ALL_ALLOWED_CHARACTERS = LOWER + UPPER + DIGITS + SPECIAL_CHARACTERS;
    private final int LENGTH = 12;
    private final String regexPassword =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*])(?=\\S+$).{12,}$";
    private static final SecureRandom random = new SecureRandom();

    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(LENGTH);

        // ensure all required character presence
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // fill the rest with random character
        for (int i = 4; i < LENGTH; i++) {
            password.append(ALL_ALLOWED_CHARACTERS.charAt(random.nextInt(ALL_ALLOWED_CHARACTERS.length())));
        }

        // shuffle
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = i + random.nextInt(passwordArray.length - i);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    public void checkPasswordValidity(String password)
            throws IllegalArgumentException {
        if (!password.matches(regexPassword))
            throw new IllegalArgumentException();
    }

}
