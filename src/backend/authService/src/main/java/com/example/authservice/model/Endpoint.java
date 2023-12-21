package com.example.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

    GET_V1_EMPLOYEES(HttpMethod.GET, Path.V1_EMPLOYEES, Permission.CanReadEmployee),
    GET_V1_EMPLOYEES_ID(HttpMethod.GET, Path.V1_EMPLOYEES_ANYONE, Permission.CanReadEmployee),
    POST_V1_EMPLOYEES(HttpMethod.POST, Path.V1_EMPLOYEES, Permission.CanCreateEmployee),
    PATCH_V1_EMPLOYEES_ROLES(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_ROLES, Permission.CanModifyEmployeeRoles),
    PATCH_V1_EMPLOYEES_PASSWORD(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_PASSWORD, Permission.CanModifyEmployeePassword),
    PATCH_V1_EMPLOYEES_ENABLE(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_ENABLE, Permission.CanDeactivateEmployee),

    GET_V1_ROLES(HttpMethod.GET, Path.V1_ROLES, Permission.CanReadRole),
    GET_V1_ROLES_ID(HttpMethod.GET, Path.V1_ROLES_ANYONE, Permission.CanReadRole),
    POST_V1_ROLES(HttpMethod.POST, Path.V1_ROLES, Permission.CanCreateRole),
    PATCH_V1_ROLES_PERMISSIONS(HttpMethod.PATCH, Path.V1_ROLES_ANYONE_PERMISSIONS, Permission.CanModifyRolePermissions),
    PATCH_V1_ROLES_ENABLE(HttpMethod.PATCH, Path.V1_ROLES_ANYONE_ENABLE, Permission.CanDeactivateRole),

    GET_V1_HELLO(HttpMethod.GET, Path.V1_HELLO, null);

    private final HttpMethod httpMethod;
    private final String path;
    private final Permission permission;

}
