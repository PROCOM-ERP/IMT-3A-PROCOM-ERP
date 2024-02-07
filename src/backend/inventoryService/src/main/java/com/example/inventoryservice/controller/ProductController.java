package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDto;
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
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory/product/")
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
                    "Role information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<Optional<ProductDto>> getProductById(@PathVariable int id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/all")
    @Operation(operationId = "getProductById", tags = {"product", "inventory"},
            summary = "Returns the product information",
            description = "Returns the product with the associated categories and items",
            parameters = {@Parameter(
                    name = "id",
                    description = "id of the product")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Role information retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "404", description =
                    "Role not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
