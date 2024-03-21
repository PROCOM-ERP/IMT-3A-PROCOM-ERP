package com.example.inventoryservice.controller;

import com.example.inventoryservice.InventoryServiceApplication;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dtoRequest.MoveItemRequestDto;
import com.example.inventoryservice.dtoRequest.NewItemRequestDto;
import com.example.inventoryservice.dtoRequest.ProductRequestDto;
import com.example.inventoryservice.dtoRequest.QuantityUpdateRequestDto;
import com.example.inventoryservice.model.Path;
import com.example.inventoryservice.service.ProductService;
import com.example.inventoryservice.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping(Path.V1_PRODUCTS)
@Validated
public class ProductController {

    private final ProductService productService;
    private final ItemService itemService;
    //private final Logger logger = LoggerFactory.getLogger(InventoryServiceApplication.class);

    @GetMapping(Path.PRODUCT_ID)
    @Operation(operationId = "getProductById", tags = {"product", "inventory"},
            summary = "Returns the product information",
            description = "Returns the product with the associated categories and items",
            parameters = {@Parameter(
                    name = "id",
                    description = "id of the product")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Product information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Product not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    @Operation(operationId = "getAllProducts", tags = {"product", "inventory"},
            summary = "Returns the product information",
            description = "Returns the product with the associated categories and items",
            parameters = {@Parameter(
                    name = "id",
                    description = "id of the product")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Product information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    @Validated
    @Operation(operationId = "createProduct", tags = {"product", "inventory"},
            summary = "Creates a new product",
            description = "Creates a new product according to the ProductRequestDto data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Product created correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Product not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Incorrect injected data. (Address or Category does not exists)",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public @ResponseBody ResponseEntity<String> createProduct(@Valid @RequestBody ProductRequestDto newProduct){
        productService.createProduct(newProduct);
        return ResponseEntity.ok().build();
    }


    @PostMapping(Path.ADD)
    @Validated
    @Operation(operationId = "addNewItem", tags = {"product", "inventory"},
            summary = "Creates a new item in this product",
            description = "Creates a item in this product and creates a new transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "item created correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemDto.class))} ),
            @ApiResponse(responseCode = "400", description =
                    "Bad request. Cannot process that request",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Entity not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Incorrect injected data. Cannot process that data",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> addNewItem(@Valid @RequestBody NewItemRequestDto newItem){
        itemService.addNewItem(newItem);
        return ResponseEntity.ok().build();
    }

    @PutMapping(Path.UPDATE)
    @Validated
    @Operation(operationId = "updateQuantity", tags = {"product", "inventory"},
            summary = "Update the quantity of the Item",
            description = "Update the quantity of the Item and creates a new transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "item updated correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemDto.class))} ),
            @ApiResponse(responseCode = "400", description =
                    "Bad request. Cannot process that request",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Entity not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Incorrect injected data. Cannot process that data",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> updateQuantity(@Valid @RequestBody QuantityUpdateRequestDto quantityUpdate){
        itemService.updateQuantity(quantityUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping(Path.MOVE)
    @Validated
    @Operation(operationId = "moveToAddress", tags = {"product", "inventory"},
            summary = "Transfers the quantity of items to another address",
            description = "Transfers the quantity of items to another address and creates a new transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "item transferred correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemDto.class))} ),
            @ApiResponse(responseCode = "400", description =
                    "Bad request. Cannot process that request",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Entity not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Incorrect injected data. Cannot process that data",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> moveToAddress(@Valid @RequestBody MoveItemRequestDto newQuantity, BindingResult bindingResult){
        itemService.moveToAddress(newQuantity);
        return ResponseEntity.ok().build();
    }
}
