package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.dto.InventoryListDto;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.model.AttributeName;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Optional<InventoryDto> getInventoryById (int inventoryId) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(inventoryId);
        return inventoryOptional
                .map(this::inventoryToDto);
    }

    public List<InventoryDto> getInventoryByGroupId(int groupId) {
        List<Inventory> inventoryList = inventoryRepository.findAllByGroupId(groupId);
            return inventoryList.stream()
                    .map(this::inventoryToDto)
                    .collect(Collectors.toList());
    }

    private InventoryDto inventoryToDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .description(inventory.getDescription())
                .attributeNames(inventory.getAttributeNames())
                .items(inventory.getItems())
                .build();
    }

    private InventoryListDto inventoryDtoList(List<Inventory> inventoryList) {
        return InventoryListDto.builder()
                .inventoryList(inventoryList)
                .build();
    }
}
