package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dtoRequest.ProductRequestDto;
import com.example.inventoryservice.model.*;
import com.example.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMetaService productMetaService;
    private final AddressService addressService;

    public Optional<ProductDto> getProductById(int id){
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.map(ProductService::productToDto);
    }

    public List<ProductDto> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(ProductService::productCategoryToDto)
                .toList();
    }

    public void createProduct(ProductRequestDto productRequest){

        Product product = Product.builder()
                .title(productRequest.getTitle())
                .description(productRequest.getDescription())
                .build();

        List<Category> categories = categoryService.getCategoriesByIds(productRequest.getCategories());
        for(Category category : categories) {
            category.getProducts().add(product);
        }

        List<ProductMeta> productMetaList = productRequest.getProductMeta().stream()
                .map(metaDto -> ProductMeta.builder()
                        .key(metaDto.getKey())
                        .value(metaDto.getValue())
                        .type(metaDto.getType())
                        .description(metaDto.getDescription())
                        .product(product)
                        .build())
                .collect(Collectors.toList());

        Optional<AddressDto> optionalAddressDto = addressService.getAddressById(productRequest.getAddress());
        Item item = Item.builder()
                .quantity(productRequest.getNumberOfItem())
                .address()
                .transactions()
                .build();
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        product.setProductMeta(productMetaList);
        product.setCategories(categories);
        //product.setItems(itemList);
        // Add Item here
        productRepository.save(product);
    }

    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .categories(categoryToDtoList(product.getCategories()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .build();
    }

    static ProductDto productItemToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .build();
    }

    static ProductDto productCategoryToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .categories(categoryToDtoList(product.getCategories()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .build();
    }

    static ProductDto productNoCategoryToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .build();
    }

    static List<ItemDto> itemToDtoList(List<Item> items){
        return items.stream()
                .map(ItemService::itemAddressToDto)
                .toList();
    }

    static List<CategoryDto> categoryToDtoList(List<Category> category){
        return category.stream()
                .map(CategoryService::categoryOnlyToDto)
                .toList();
    }

    static List<ProductMetaDto> productMetaToDtoList(List<ProductMeta> productMeta){
        return productMeta.stream()
                .map(ProductMetaService::productMetaToDto)
                .toList();
    }
}
