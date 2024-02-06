package com.example.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {
/*
    private final InventoryService inventoryService;
    /**
     * Function that return all the items of a specific inventory.
     *
     * @return List of ItemsDto.
     *//*
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable int id) {
        return inventoryService.getInventoryById(id)
                .map(inventoryDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(inventoryDto))
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build());
    }

    /**
     * Function that return all the items of a specific inventory.
     *
     * @return List of ItemsDto.
     */
    /*@GetMapping("/{id}")
    public ResponseEntity<InventoryListDto> getInventoryByGroupId(@PathVariable int groupId) {
        return inventoryService.getInventoryByGroupId(groupId)
                .stream()
                .map(inventoryDto ->
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(inventoryDto))
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build());

    }*/



    /**
     * Function that return all the users of the selected inventory.
     * @return List of ItemsDto.
     *//*
    public ResponseEntity<List<ItemDto>> getAllInventory(int inventoryId){
        return null; // to do
    }

    public ResponseEntity<String> renameColumn(int attributeNameId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .build(); // to do
    }

*/
}
