package com.example.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

    GET_V1_EMPLOYEES_ID(HttpMethod.GET, Path.V1_EMPLOYEES_ANYONE, Permission.CanReadEmployee),

    GET_V1_PROVIDERS(HttpMethod.GET, Path.V1_PROVIDERS, Permission.CanReadProvider),

    GET_V1_ORDERS(HttpMethod.GET, Path.V1_ORDERS, Permission.CanReadOrder),
    GET_V1_ORDERS_ID(HttpMethod.GET, Path.V1_ORDERS_ANYONE, Permission.CanReadOrder),
    PATCH_V1_ORDERS_ANYTHING(HttpMethod.PATCH, Path.V1_ORDERS_ANYTHING, Permission.CanModifyOrder),
    POST_V1_ORDERS(HttpMethod.POST, Path.V1_ORDERS, Permission.CanCreateOrder),

    GET_V1_ROLES(HttpMethod.GET, Path.V1_ROLES, Permission.CanReadRole),
    GET_V1_ROLES_ID(HttpMethod.GET, Path.V1_ROLES_ANYONE, Permission.CanReadRole),
    GET_V1_ROLES_ANYTHING(HttpMethod.GET, Path.V1_ROLES_ANYTHING, Permission.CanReadRole),
    PUT_V1_ROLES_ID(HttpMethod.PUT, Path.V1_ROLES_ANYONE, Permission.CanModifyRole),

    GET_V1_HELLO(HttpMethod.GET, Path.V1_HELLO, null),

    GET_V1_DOCS(HttpMethod.GET, Path.V1_DOCS_ANYTHING, null);

    private final HttpMethod httpMethod;
    private final String path;
    private final Permission permission;
}
