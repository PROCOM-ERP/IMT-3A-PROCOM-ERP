package com.example.inventoryservice.dto;

import com.example.inventoryservice.model.Attribute;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@AllArgsConstructor
public class ItemDto {
    private int id;
    private String name;
    private int inventoryId;
    private String alias;
    private String status;
    private String arrivalDate;
    private String removalDate;
    //Custom attributes:
    private List<Attribute> attribute;
}
