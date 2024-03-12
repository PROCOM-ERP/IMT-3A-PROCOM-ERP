package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dtoRequest.CategoryRequestDto;
import com.example.inventoryservice.model.Path;
import com.example.inventoryservice.service.CategoryService;
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
@RequestMapping(Path.V1_CATEGORIES)
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping(Path.CATEGORY_ID)
    @Operation(operationId = "getCategoryById", tags = {"categories", "inventory"},
            summary = "Returns one category",
            description = "Returns the category with the associated information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Category retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "404", description =
                    "Entity not found",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    @Operation(operationId = "getAllCategory", tags = {"categories", "inventory"},
            summary = "Returns all the categories",
            description = "Returns all the categories without the detailed information about the joint products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "All categories retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductDto.class))} ),
            @ApiResponse(responseCode = "403", description =
                    "This controller has been closed",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<List<CategoryDto>> getAllCategory(){
        //return ResponseEntity.ok(categoryService.getAllCategories());
        return ResponseEntity.status(403).body(null); // <- This endpoint is closed.
    }

    @PostMapping
    @Operation(operationId = "createCategory", tags = {"category", "inventory"},
            summary = "Creates a new category",
            description = "Creates a new category according to the CategoryRequestDto data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Category created correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "400", description =
                    "Bad request. Cannot process that request",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "422", description =
                    "Incorrect injected data.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequestDto newCategory){
        categoryService.createCategory(newCategory);
        return ResponseEntity.ok().build();
    }
}
