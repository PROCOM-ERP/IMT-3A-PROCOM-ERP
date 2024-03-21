package com.example.authenticationservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CustomPasswordUtils {

    private final PasswordEncoder passwordEncoder;

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#$%^+=!*";
    private static final String ALL_ALLOWED_CHARACTERS = LOWER + UPPER + DIGITS + SPECIAL_CHARACTERS;
    private static final int LENGTH = 12;
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

    public String hashPassword(String password)
    {
        return passwordEncoder.encode(password);
    }
}
