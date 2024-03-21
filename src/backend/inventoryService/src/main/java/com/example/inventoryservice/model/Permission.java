package com.example.inventoryservice.model;

public enum Permission {

    // Roles permissions
    CanBypassAccessDeny,

    CanModifyRole,

    CanReadRole,

    // Inventory service permissions:
    CanReadInventories,

    CanManageInventories,

    CanCreateCategories
}
