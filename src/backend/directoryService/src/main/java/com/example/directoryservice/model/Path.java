package com.example.directoryservice.model;

public class Path {

    public static final String V1 = "/api/v1";
    public static final String ANYONE = "/*";
    public static final String ANYTHING = "/**";
    public static final String HELLO = "/hello";
    public static final String DOCS = "/docs";
    public static final String ENABLE = "/enable";
    public static final String ROLES = "/roles";
    public static final String PERMISSIONS = "/permissions";

    public static final String V1_ROLES = V1 + ROLES;
    public static final String ROLE_NAME = "/{role}";
    public static final String ROLE_NAME_PERMISSIONS = ROLE_NAME + PERMISSIONS;
    public static final String ROLE_NAME_ENABLE = ROLE_NAME + ENABLE;

    public static final String V1_ROLES_ANYONE = V1_ROLES + ANYONE;
    public static final String V1_ROLES_ANYONE_PERMISSIONS = V1_ROLES_ANYONE + PERMISSIONS;
    public static final String V1_ROLES_ANYONE_ENABLE = V1_ROLES_ANYONE + ENABLE;

    public static final String V1_PERMISSIONS = V1 + PERMISSIONS;

    public static final String V1_HELLO = V1 + HELLO;

    public static final String V1_DOCS_ANYTHING = V1 + DOCS + ANYTHING;

}
