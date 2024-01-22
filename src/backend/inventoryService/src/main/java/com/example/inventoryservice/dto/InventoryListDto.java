package com.example.inventoryservice.dto;


import com.example.inventoryservice.model.Inventory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@AllArgsConstructor
public class InventoryListDto {
    private List<Inventory> inventoryList;
}

