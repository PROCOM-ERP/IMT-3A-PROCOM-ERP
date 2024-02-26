package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.model.Address;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    final private AddressRepository addressRepository;

    /**
     * Function that retrieve all the categories
     * @return List<AddressDto>
     */
    public List<AddressDto> getAllAddress(){
        return addressRepository.findAll()
                .stream()
                .map(AddressService::addressOnlyToDto)
                .toList();
    }

    /**
     * Function that retrive on address with his associated id as parameter
     * This function is made for the other services
     * @param id: id of the selected address
     * @return Address
     * @throws NoSuchElementException "The address Id refers to a non-existent address"
     */
    public Address getAddressById(int id) throws NoSuchElementException{
        return addressRepository.findById(id).orElseThrow();
    }

    /**
     * Function that retrive on address as dto format with his associated id as parameter.
     * This is supposed to be returned to the controller
     * @param id: id of the selected address
     * @return AddressDto
     */
    public AddressDto getAddressDtoById(int id) {
        return addressRepository.findById(id).map(AddressService::addressToDto).orElseThrow(
                () -> new NoSuchElementException("The address Id refers to a non existent address"));
    }

    // DTO converters:

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
