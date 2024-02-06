package com.example.inventoryservice.dtoProduct;

import com.example.inventoryservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    private Integer quantity;
    private List<TransactionDto> transactions;
    private AddressDto address;
}
