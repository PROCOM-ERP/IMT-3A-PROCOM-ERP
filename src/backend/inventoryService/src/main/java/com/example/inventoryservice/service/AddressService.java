package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.model.Address;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    final private AddressRepository addressRepository;

    static AddressDto addressToDto(Address address){
        return AddressDto.builder()
                .id(address.getId())
                .city(address.getCity())
                .country(address.getCountry())
                .state(address.getState())
                .number(address.getNumber())
                .items(itemToDtoList(address.getItems()))

                .build();
    }

    static List<ItemDto> itemToDtoList(List<Item> item){
        return item.stream()
                .map(ItemService::ItemToDto)
                .toList();
    }
}
