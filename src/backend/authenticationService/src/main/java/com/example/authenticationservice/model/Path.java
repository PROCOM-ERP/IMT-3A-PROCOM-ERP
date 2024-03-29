package com.example.authenticationservice.model;

public class Path {

    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String ANYONE = "/*";
    public static final String ANYTHING = "/**";
    public static final String HELLO = "/hello";
    public static final String DOCS = "/docs";
    public static final String LOGIN_PROFILES = "/login-profiles";
    public static final String PASSWORD = "/password";
    public static final String ROLES = "/roles";
    public static final String ACTIVATION = "/activation";
    public static final String MICROSERVICES = "/microservices";
    public static final String AUTH = "/auth";

    public static final String V1_LOGIN_PROFILES = API + V1 + LOGIN_PROFILES;
    public static final String LOGIN_PROFILE_ID = "/{idLoginProfile}";
    public static final String LOGIN_PROFILE_ID_PASSWORD = LOGIN_PROFILE_ID + PASSWORD;
    public static final String LOGIN_PROFILE_ID_ACTIVATION = LOGIN_PROFILE_ID + ACTIVATION;

    public static final String V1_LOGIN_PROFILES_ANYONE = V1_LOGIN_PROFILES + ANYONE;
    public static final String V1_LOGIN_PROFILES_ANYONE_PASSWORD = V1_LOGIN_PROFILES_ANYONE + PASSWORD;
    public static final String V1_LOGIN_PROFILES_ANYONE_ACTIVATION = V1_LOGIN_PROFILES_ANYONE + ACTIVATION;

    public static final String V1_ROLES = API + V1 + ROLES;
    public static final String ROLE_NAME = "/{role}";
    public static final String ROLE_NAME_ACTIVATION = ROLE_NAME + ACTIVATION;

    public static final String V1_ROLES_ANYONE = V1_ROLES + ANYONE;
    public static final String V1_ROLES_MICROSERVICES = V1_ROLES + MICROSERVICES;
    public static final String V1_ROLES_ANYTHING = V1_ROLES + ANYTHING;

    public static final String V1_AUTH = API + V1 + AUTH;
    public static final String JWT = "/jwt";

    public static final String V1_HELLO = API + V1 + HELLO;

    public static final String V1_DOCS_ANYTHING = API + V1 + DOCS + ANYTHING;
}
