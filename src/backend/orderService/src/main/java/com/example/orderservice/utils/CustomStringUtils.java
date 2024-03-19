package com.example.orderservice.utils;

import org.springframework.stereotype.Component;

@Component
public class CustomStringUtils {

    public static final String REGEX_ID_LOGIN_PROFILE =
            "^[A-Z][0-9]{5}$";
    public static final String REGEX_ROLE_NAME =
            "^[a-zA-Z]([\\-\\.]?[a-zA-Z0-9])*$";
    public static final String REGEX_EMPLOYEE_LAST_NAME =
            "^'?[\\p{L}](['\\.\\-]? ?[\\p{L}])*['\\.]?$";
    public static final String REGEX_EMPLOYEE_FIRST_NAME =
            "^'?[\\p{L}](['\\.\\-]? ?[\\p{L}])*['\\.]?$";
    public static final String REGEX_EMPLOYEE_EMAIL =
            "^[a-z0-9]([\\-\\.]?[a-z0-9])*@[a-z0-9]([\\-\\.]?[a-z0-9])*$";
    public static final String REGEX_EMPLOYEE_PHONE_NUMBER =
            "^\\+?[0-9]{1,3}[\\- ]?([0-9]{1,4}[\\- ]?)*[0-9]{1,4}$";
    public static final String REGEX_ORDER_QUOTE =
            "^[a-zA-Z0-9]([_\\- ]?[a-zA-Z0-9])*$";
    public static final String REGEX_ORDER_PRODUCT_REFERENCE =
            "^[a-zA-Z0-9]([_\\- ]?[a-zA-Z0-9])*$";

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

    public void checkStringSize(String input, String message, int min, int max)
            throws IllegalArgumentException
    {
        if (input.length() > max && input.length() < min)
            throw new IllegalArgumentException(message);
    }

    public void checkStringPattern(String input, String regex, String message)
            throws IllegalArgumentException
    {
        if (input == null || ! input.matches(regex))
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
