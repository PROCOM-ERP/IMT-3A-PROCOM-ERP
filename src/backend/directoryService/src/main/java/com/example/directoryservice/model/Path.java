package com.example.directoryservice.model;

public class Path {

    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String ANYONE = "/*";
    public static final String ANYTHING = "/**";
    public static final String ACTIVATION = "/activation";

    public static final String ADDRESSES = "/addresses";
    public static final String V1_ADDRESSES = API + V1 + ADDRESSES;

    public static final String ORGANISATIONS = "/organisations";
    public static final String V1_ORGANISATIONS = API + V1 + ORGANISATIONS;

    public static final String EMPLOYEES = "/employees";
    public static final String EMPLOYEE_ID = "/{idEmployee}";
    public static final String V1_EMPLOYEES = API + V1 + EMPLOYEES;
    public static final String V1_EMPLOYEES_ANYONE = V1_EMPLOYEES + ANYONE;

    public static final String ROLES = "/roles";
    public static final String V1_ROLES = API + V1 + ROLES;
    public static final String ROLE_NAME = "/{role}";
    public static final String ROLE_NAME_ACTIVATION = ROLE_NAME + ACTIVATION;
    public static final String V1_ROLES_ANYONE = V1_ROLES + ANYONE;
    public static final String V1_ROLES_ANYTHING = V1_ROLES + ANYTHING;

    public static final String HELLO = "/hello";
    public static final String V1_HELLO = API + V1 + HELLO;

    public static final String DOCS = "/docs";
    public static final String V1_DOCS_ANYTHING = API + V1 + DOCS + ANYTHING;

}
