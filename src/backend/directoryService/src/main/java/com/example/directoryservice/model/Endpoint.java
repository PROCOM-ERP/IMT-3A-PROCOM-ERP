package com.example.directoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

    GET_V1_ADDRESSES(HttpMethod.GET, Path.V1_ADDRESSES, Permission.CanReadAddress),
    POST_V1_ADDRESSES(HttpMethod.POST, Path.V1_ADDRESSES, Permission.CanCreateAddress),

    GET_V1_ORGANISATIONS(HttpMethod.GET, Path.V1_ORGANISATIONS, Permission.CanReadOrganisation),

    GET_V1_EMPLOYEES(HttpMethod.GET, Path.V1_EMPLOYEES, Permission.CanReadEmployee),
    GET_V1_EMPLOYEES_ID(HttpMethod.GET, Path.V1_EMPLOYEES_ANYONE, Permission.CanReadEmployee),
    POST_V1_EMPLOYEES(HttpMethod.GET, Path.V1_EMPLOYEES, Permission.CanCreateEmployee),
    PUT_V1_EMPLOYEES_ID(HttpMethod.PUT, Path.V1_EMPLOYEES_ANYONE, Permission.CanModifyEmployee),

    GET_V1_ROLES(HttpMethod.GET, Path.V1_ROLES, Permission.CanReadRole),
    GET_V1_ROLES_ID(HttpMethod.GET, Path.V1_ROLES_ANYONE, Permission.CanReadRole),
    GET_V1_ROLES_ANYTHING(HttpMethod.GET, Path.V1_ROLES_ANYTHING, Permission.CanReadRole),
    POST_V1_ROLES(HttpMethod.POST, Path.V1_ROLES, Permission.CanCreateRole),
    PUT_V1_ROLES_ID(HttpMethod.PUT, Path.V1_ROLES_ANYONE, Permission.CanModifyRole),

    GET_V1_HELLO(HttpMethod.GET, Path.V1_HELLO, null),

    GET_V1_DOCS(HttpMethod.GET, Path.V1_DOCS_ANYTHING, null);

    private final HttpMethod httpMethod;
    private final String path;
    private final Permission permission;
}
