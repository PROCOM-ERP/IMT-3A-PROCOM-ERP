package com.example.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {
    /**
     * Inventory Service endpoints:
     */
    // CATEGORIES
    GET_V1_CATEGORIES(HttpMethod.GET, Path.V1_CATEGORIES, Permission.CanReadInventories),
    GET_V1_CATEGORY_ID(HttpMethod.GET, Path.V1_CATEGORY_ID, Permission.CanReadInventories),
    POST_V1_CATEGORIES(HttpMethod.GET, Path.V1_CATEGORIES, Permission.CanCreateCategories),

    // PRODUCTS
    GET_V1_PRODUCTS(HttpMethod.GET, Path.V1_PRODUCTS, Permission.CanReadInventories),
    GET_V1_PRODUCT_ID(HttpMethod.GET, Path.V1_PRODUCT_ID, Permission.CanReadInventories),
    POST_V1_PRODUCTS(HttpMethod.GET, Path.V1_PRODUCTS, Permission.CanManageInventories),
    POST_V1_PRODUCTS_ADD(HttpMethod.GET, Path.V1_PRODUCTS, Permission.CanManageInventories),
    POST_V1_PRODUCTS_UPDATE(HttpMethod.GET, Path.V1_PRODUCTS, Permission.CanManageInventories),
    POST_V1_PRODUCTS_MOVE(HttpMethod.GET, Path.V1_PRODUCTS, Permission.CanManageInventories),


    // OTHER:
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
