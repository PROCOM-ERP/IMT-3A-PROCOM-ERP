package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/item")
public class ItemController {

    private final ItemService itemService;
    /**
     * Function that return all the items of a specific item.
     *
     * @return List of ItemsDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable int id) {
        return itemService.getItemById(id)
                .map(itemDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(itemDto))
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build());
    }



    /**
     * Function that return all the users of the selected item.
     * @return List of ItemsDto.
     */
    public ResponseEntity<List<ItemDto>> getAllItem(int itemId){
        return null; // to do
    }

    public ResponseEntity<String> renameColumn(int attributeNameId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .build(); // to do
    }


}
