package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dtoRequest.ProductRequestDto;
import com.example.inventoryservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
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
                            schema = @Schema(type = "array", implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Product not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
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
    public ResponseEntity<String> createProduct(@RequestBody ProductRequestDto newProduct){
        productService.createProduct(newProduct);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuantity(@RequestBody NewQuantityRequestDto newQuantoty){
        pass
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateQuantity(@RequestBody QuantityUpdateRequestDto quantityUpdate){
        pass
    }
}
