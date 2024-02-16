package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.model.Address;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    final private AddressRepository addressRepository;

    public List<AddressDto> getAllAddress(){
        return addressRepository.findAll()
                .stream()
                .map(AddressService::addressOnlyToDto)
                .toList();
    }

    public Optional<AddressDto> getOptionalAddressById(int id){
        Optional<Address> addressOptional = addressRepository.findById(id);
        return addressOptional.map(AddressService::addressToDto);
    }

    public Address getAddressById(int id){
        return addressRepository.findById(id).orElseThrow();
    }

    static AddressDto addressToDto(Address address){
        return AddressDto.builder()
                .id(address.getId())
                .city(address.getCity())
                .country(address.getCountry())
                .state(address.getState())
                .number(address.getNumber())
                .info(address.getInfo())
                .street(address.getStreet())
                .postal_code(address.getPostalCode())
                .items(itemToDtoList(address.getItems()))
                .build();
    }

    static AddressDto addressOnlyToDto(Address address){
        return AddressDto.builder()
                .id(address.getId())
                .city(address.getCity())
                .country(address.getCountry())
                .state(address.getState())
                .number(address.getNumber())
                .info(address.getInfo())
                .street(address.getStreet())
                .postal_code(address.getPostalCode())
                .build();
    }

    static List<ItemDto> itemToDtoList(List<Item> item){
        return item.stream()
                .map(ItemService::itemProductToDto)
                .toList();
    }
}
