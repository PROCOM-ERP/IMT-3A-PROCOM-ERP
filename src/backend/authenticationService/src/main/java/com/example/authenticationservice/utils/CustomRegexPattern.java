package com.example.authenticationservice.utils;

public class CustomRegexPattern {

    public static final String REGEX_ID_LOGIN_PROFILE =
            "^[A-Z][0-9]{5}$";
    public static final String REGEX_EMAIL =
            "^[a-z0-9]([\\-\\.]?[a-z0-9])*@[a-z0-9]([\\-\\.]?[a-z0-9])*$";
    public static final String REGEX_PHONE_NUMBER =
            "";
    public static final String REGEX_PASSWORD =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*])(?=\\S+$).{12,}$";
}
