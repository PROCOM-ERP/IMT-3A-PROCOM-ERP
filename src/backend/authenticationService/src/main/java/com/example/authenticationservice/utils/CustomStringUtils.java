package com.example.authenticationservice.utils;

import org.springframework.stereotype.Component;

@Component
public class CustomStringUtils {

    public String nullifyBlankString(String input) {
        return input == null || input.isBlank() ? null : input;
    }

    public String escapeHtmlChars(String input) {
        if (input == null)
            return null;

        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }
}
