package com.example.inventoryservice.dto;

import com.example.inventoryservice.model.Attribute;
import com.example.inventoryservice.model.AttributeName;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.model.Item;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@AllArgsConstructor
public class InventoryDto {
    private int id;
    private String name;
    private String description;
    private boolean isActive;

    //Custom attributes:
    private List<Item> items;
    private List<AttributeName> attributeNames;
}
