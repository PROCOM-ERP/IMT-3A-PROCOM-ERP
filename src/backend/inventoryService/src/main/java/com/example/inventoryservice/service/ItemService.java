package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Optional<ItemDto> getItemById (int itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        return itemOptional
                .map(this::itemToDto);
    }

    public List<ItemDto> getItemByGroupId(int groupId) {
        List<Item> itemList = itemRepository.findAllByInventoryId(groupId);
            return itemList.stream()
                    .map(this::itemToDto)
                    .collect(Collectors.toList());
    }

    private ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .alias(item.getAlias())
                .status(item.getStatus())
                .description(item.getDescription())
                .attributeNames(item.getAttributeNames())
                .items(item.getItems())
                .build();
    }
}
