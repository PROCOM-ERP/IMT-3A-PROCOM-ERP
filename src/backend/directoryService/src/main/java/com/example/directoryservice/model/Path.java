package com.example.directoryservice.model;

public class Path {

    public static final String V1 = "/api/v1";
    public static final String ANYONE = "/*";
    public static final String ANYTHING = "/**";
    public static final String HELLO = "/hello";
    public static final String DOCS = "/docs";
    public static final String ENABLE = "/enable";
    public static final String ADDRESS = "/address";
    public static final String ADDRESSES = "/addresses";
    public static final String ORGANISATION = "/organisation";
    public static final String ORGANISATIONS = "/organisations";
    public static final String SERVICE = "/service";
    public static final String SERVICES = "/services";
    public static final String EMPLOYEES = "/employees";
    public static final String INFO = "/info";
    public static final String ROLES = "/roles";
    public static final String PERMISSIONS = "/permissions";

    public static final String V1_ADDRESSES = V1 + ADDRESSES;
    public static final String ADDRESS_ID = "/{idAddress}";
    public static final String V1_ADDRESSES_ANYONE = V1_ADDRESSES + ANYONE;

    public static final String V1_ORGANISATIONS = V1 + ORGANISATIONS;
    public static final String ORGANISATION_ID_OR_NAME = "/{idOrName}";
    public static final String ORGANISATION_ID_OR_NAME_ADDRESS = ORGANISATION_ID_OR_NAME + ADDRESS;
    public static final String V1_ORGANISATIONS_ANYONE = V1_ORGANISATIONS + ANYONE;
    public static final String V1_ORGANISATIONS_ANYONE_ADDRESS = V1_ORGANISATIONS_ANYONE + ADDRESS;

    public static final String V1_SERVICES = V1 + SERVICES;
    public static final String SERVICE_ID = "/{idService}";
    public static final String SERVICE_ID_ADDRESS = SERVICE_ID + ADDRESS;
    public static final String SERVICE_ID_ORGANISATION = SERVICE_ID + ORGANISATION;
    public static final String V1_SERVICES_ANYONE = V1_SERVICES + ANYONE;
    public static final String V1_SERVICES_ANYONE_ADDRESS = V1_SERVICES_ANYONE + ADDRESS;
    public static final String V1_SERVICES_ANYONE_ORGANISATION = V1_SERVICES_ANYONE + ORGANISATION;

    public static final String V1_EMPLOYEES = V1 + EMPLOYEES;
    public static final String EMPLOYEE_ID_OR_EMAIL = "/{idOrEmail}";
    public static final String EMPLOYEE_ID_OR_EMAIL_ENABLE = EMPLOYEE_ID_OR_EMAIL + ENABLE;
    public static final String EMPLOYEE_ID_OR_EMAIL_INFO = EMPLOYEE_ID_OR_EMAIL + INFO;
    public static final String EMPLOYEE_ID_OR_EMAIL_SERVICE = EMPLOYEE_ID_OR_EMAIL + SERVICE;
    public static final String V1_EMPLOYEES_ANYONE = V1_EMPLOYEES + ANYONE;
    public static final String V1_EMPLOYEES_ANYONE_ENABLE = V1_EMPLOYEES_ANYONE + ENABLE;
    public static final String V1_EMPLOYEES_ANYONE_INFO = V1_EMPLOYEES_ANYONE + INFO;
    public static final String V1_EMPLOYEES_ANYONE_SERVICE = V1_EMPLOYEES_ANYONE + SERVICE;

    public static final String V1_ROLES = V1 + ROLES;
    public static final String ROLE_NAME = "/{role}";
    public static final String ROLE_NAME_ENABLE = ROLE_NAME + ENABLE;
    public static final String ROLE_NAME_PERMISSIONS = ROLE_NAME + PERMISSIONS;
    public static final String V1_ROLES_ANYONE = V1_ROLES + ANYONE;
    public static final String V1_ROLES_ANYONE_ENABLE = V1_ROLES_ANYONE + ENABLE;
    public static final String V1_ROLES_ANYONE_PERMISSIONS = V1_ROLES_ANYONE + PERMISSIONS;

    public static final String V1_PERMISSIONS = V1 + PERMISSIONS;

    public static final String V1_HELLO = V1 + HELLO;

    public static final String V1_DOCS_ANYTHING = V1 + DOCS + ANYTHING;

}