package com.example.inventoryservice.model;

public class Path {

    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String ANYONE = "/*";
    public static final String ANYTHING = "/**";
    public static final String ACTIVATION = "/activation";
    public static final String CREATE = "/create";
    public static final String ADD = "/add";
    public static final String UPDATE = "/update";
    public static final String MOVE = "/move";

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


    // Categories:
    public static final String CATEGORIES = "/categories";
    public static final String CATEGORY_ID = "/{id}";
    public static final String V1_CATEGORIES = API + V1 + CATEGORIES;
    public static final String V1_CATEGORY_ID = V1_CATEGORIES + CATEGORY_ID;
    public static final String V1_CATEGORY_CREATE = V1_CATEGORIES + CREATE;


    // Products:
    public static final String PRODUCTS = "/products";
    public static final String PRODUCT_ID = "/{id}";
    public static final String V1_PRODUCTS = API + V1 + PRODUCTS;
    public static final String V1_PRODUCT_ID = V1_PRODUCTS + PRODUCT_ID;
    public static final String V1_PRODUCT_CREATE = V1_PRODUCTS + CREATE;
    public static final String V1_PRODUCT_ADD = V1_PRODUCTS + ADD;
    public static final String V1_PRODUCT_UPDATE = V1_PRODUCTS + UPDATE;
    public static final String V1_PRODUCT_MOVE = V1_PRODUCTS + MOVE;


    // Addresses:
    public static final String ADDRESSES = "/addresses";
    public static final String ADDRESS_ID = "/{id}";
    public static final String V1_ADDRESSES = API + V1 + ADDRESSES;
    public static final String V1_ADDRESS_ID = V1_ADDRESSES + ADDRESS_ID;
}
