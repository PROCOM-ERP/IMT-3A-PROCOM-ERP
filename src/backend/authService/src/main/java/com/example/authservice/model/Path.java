package com.example.authservice.model;

public class Path {

    public static final String V1 = "/api/v1";
    public static final String ANYONE = "/*";
    public static final String EMPLOYEES = "/employees";
    public static final String PASSWORD = "/password";
    public static final String ENABLE = "/enable";
    public static final String ROLES = "/roles";
    public static final String PERMISSIONS = "/permissions";
    public static final String AUTH = "/auth";
    public static final String HELLO = "/hello";

    public static final String V1_EMPLOYEES = V1 + EMPLOYEES;
    public static final String EMPLOYEE_ID = "/{idEmployee}";
    public static final String EMPLOYEE_ID_ROLES = EMPLOYEE_ID + ROLES;
    public static final String EMPLOYEE_ID_PASSWORD = EMPLOYEE_ID + PASSWORD;
    public static final String EMPLOYEE_ID_ENABLE = EMPLOYEE_ID + ENABLE;

    public static final String V1_EMPLOYEES_ANYONE = V1_EMPLOYEES + ANYONE;
    public static final String V1_EMPLOYEES_ANYONE_ROLES = V1_EMPLOYEES_ANYONE + ROLES;
    public static final String V1_EMPLOYEES_ANYONE_PASSWORD = V1_EMPLOYEES_ANYONE + PASSWORD;
    public static final String V1_EMPLOYEES_ANYONE_ENABLE = V1_EMPLOYEES_ANYONE + ENABLE;

    public static final String V1_ROLES = V1 + ROLES;
    public static final String ROLE = "/{role}";
    public static final String ROLE_NAME_PERMISSIONS = ROLE + PERMISSIONS;
    public static final String ROLE_NAME_ENABLE = ROLE + ENABLE;

    public static final String V1_ROLES_ANYONE = V1_ROLES + ANYONE;
    public static final String V1_ROLES_ANYONE_PERMISSIONS = V1_ROLES_ANYONE + PERMISSIONS;
    public static final String V1_ROLES_ANYONE_ENABLE = V1_ROLES_ANYONE + ENABLE;

    public static final String V1_AUTH = V1 + AUTH;
    public static final String JWT = "/jwt";

    public static final String V1_HELLO = V1 + HELLO;

}
