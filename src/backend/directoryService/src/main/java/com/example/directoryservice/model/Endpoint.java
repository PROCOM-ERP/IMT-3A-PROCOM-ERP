package com.example.directoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

    GET_V1_ADDRESSES(HttpMethod.GET, Path.V1_ADDRESSES, Permission.CanReadAddress),
    GET_V1_ADDRESSES_ID(HttpMethod.GET, Path.V1_ADDRESSES_ANYONE, Permission.CanReadAddress),
    POST_V1_ADDRESSES(HttpMethod.POST, Path.V1_ADDRESSES_ANYONE, Permission.CanCreateAddress),
    DELETE_V1_ADDRESSES_ID(HttpMethod.DELETE, Path.V1_ADDRESSES_ANYONE, Permission.CanDeleteAddress),

    GET_V1_ORGANISATIONS(HttpMethod.GET, Path.V1_ORGANISATIONS, Permission.CanReadOrganisation),
    GET_V1_ORGANISATIONS_ID(HttpMethod.GET, Path.V1_ORGANISATIONS_ANYONE, Permission.CanReadOrganisation),
    POST_V1_ORGANISATIONS(HttpMethod.POST, Path.V1_ORGANISATIONS, Permission.CanCreateOrganisation),
    PATCH_V1_ORGANISATIONS_ID_ADDRESS(HttpMethod.PATCH, Path.V1_ORGANISATIONS_ANYONE_ADDRESS, Permission.CanModifyOrganisationAddress),
    DELETE_V1_ORGANISATIONS_ID(HttpMethod.DELETE, Path.V1_ORGANISATIONS_ANYONE, Permission.CanDeleteOrganisation),

    GET_V1_SERVICES(HttpMethod.GET, Path.V1_SERVICES, Permission.CanReadService),
    GET_V1_SERVICES_ID(HttpMethod.GET, Path.V1_SERVICES_ANYONE, Permission.CanReadService),
    POST_V1_SERVICES(HttpMethod.POST, Path.V1_SERVICES, Permission.CanCreateService),
    PATCH_V1_SERVICES_ID_ADDRESS(HttpMethod.PATCH, Path.V1_SERVICES_ANYONE_ADDRESS, Permission.CanModifyServiceAddress),
    PATCH_V1_SERVICES_ID_ORGANISATION(HttpMethod.PATCH, Path.V1_SERVICES_ANYONE_ORGANISATION, Permission.CanModifyServiceOrganisation),
    DELETE_V1_SERVICES_ID(HttpMethod.DELETE, Path.V1_SERVICES_ANYONE, Permission.CanDeleteService),

    GET_V1_EMPLOYEES(HttpMethod.GET, Path.V1_EMPLOYEES, Permission.CanReadEmployee),
    GET_V1_EMPLOYEES_ID(HttpMethod.GET, Path.V1_EMPLOYEES_ANYONE, Permission.CanReadEmployee),
    POST_V1_EMPLOYEES(HttpMethod.GET, Path.V1_EMPLOYEES, Permission.CanCreateEmployee),
    PATCH_V1_EMPLOYEES_ID_ENABLE(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_ENABLE, Permission.CanDeactivateEmployee),
    PATCH_V1_EMPLOYEES_ID_INFO(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_INFO, Permission.CanModifyEmployeeInfo),
    PATCH_V1_EMPLOYEES_ID_SERVICE(HttpMethod.PATCH, Path.V1_EMPLOYEES_ANYONE_SERVICE, Permission.CanModifyEmployeeService),

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
