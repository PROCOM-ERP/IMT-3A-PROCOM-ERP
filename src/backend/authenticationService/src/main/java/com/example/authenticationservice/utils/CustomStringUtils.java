package com.example.authenticationservice.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Component
public class CustomStringUtils {

    public void sanitizeAllStrings(Object obj)
    {
        if (obj == null)
            return;

        // get all Object instance fields
        Field[] fields = obj.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                // sanitize field if it is a String
                if (field.getType().equals(String.class)) { //
                    field.setAccessible(true);
                    String fieldValue = (String) field.get(obj);
                    field.set(obj, sanitizeString(fieldValue));
                    // sanitize embedded Object String fields
                } else if (!field.getType().isPrimitive()) {
                    field.setAccessible(true);
                    sanitizeAllStrings(field.get(obj));
                }
            } catch (IllegalAccessException ignored) {
            }
        });
    }

    public String sanitizeString(String input)
    {
        return escapeHtmlChars(nullifyBlankString(input.trim()));
    }

    public void checkNullOrBlankString(String input, String message)
            throws IllegalArgumentException
    {
        if (input == null || input.isBlank())
            throw new IllegalArgumentException(message);
    }

    public String nullifyBlankString(String input)
    {
        return input == null || input.isBlank() ? null : input;
    }

    public String escapeHtmlChars(String input)
    {
        if (input == null) {
            return null;
        }
        return input.trim()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
