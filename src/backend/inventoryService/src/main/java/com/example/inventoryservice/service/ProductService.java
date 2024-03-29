package com.example.inventoryservice.service;

import com.example.inventoryservice.InventoryServiceApplication;
import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dtoRequest.ProductRequestDto;
import com.example.inventoryservice.model.*;
import com.example.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final AddressService addressService;
    private final Logger logger = LoggerFactory.getLogger(InventoryServiceApplication.class);

    /**
     * Function that returns one product with all the information about this product (EAGER):
     *  - associated categories
     *  - product metadata
     *  - items of the product, with the associated addresses and transactions
     * @param id: id of the selected product
     * @return ProductDto
     * @throws NoSuchElementException: The element does not exist
     */
    public ProductDto getProductById(int id)
            throws NoSuchElementException {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.map(ProductService::productToDto).orElseThrow();
    }

    /**
     * Function that retrieve all the products without the detailed information
     * @return List<ProductDto>
     */
    public List<ProductDto> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(ProductService::productCategoryToDto)
                .toList();
    }

    /**
     * Function that creates a new product. This will create a Product. If the numberOfItem is greater than 0,
     * an object Item and transaction will be created.
     * @param productRequest : This requires to be compliant to the ProductRequestDto definition.
     */
    @Transactional
    public void createProduct(ProductRequestDto productRequest){
        Product product = Product.builder()
                .title(productRequest.getTitle())
                .description(productRequest.getDescription())
                .build();

        List<Category> categories = categoryService.getAllByIds(productRequest.getCategories());
        //LoginProfile employee = loginProfileRepository.findById(productRequest.get);

        if (categories == null || categories.isEmpty()) {
            // Error 422
            logger.error("The returned category does not exists.");
            throw new DataIntegrityViolationException("The id of the category does not exists in the database.");
        }
        for (Category category : categories) {
            category.getProducts().add(product);
        }
        if(productRequest.getProductMeta() != null && !productRequest.getProductMeta().isEmpty()){
            List<ProductMeta> productMetaList = productRequest.getProductMeta().stream()
                    .map(metaDto -> ProductMeta.builder()
                            .key(metaDto.getKey())
                            .value(metaDto.getValue())
                            .type(metaDto.getType())
                            .description(metaDto.getDescription())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());

            product.setProductMeta(productMetaList);
        }

        if ((productRequest.getNumberOfItem() > 0)){
            Address address = addressService.getAddressById(productRequest.getAddress());
            if (address == null){
                // Error 422
                logger.error("The returned address does not exists.");
                throw new DataIntegrityViolationException("The id of the address does not exists in the database.");
            }

            Item item = Item.builder()
                    .quantity(productRequest.getNumberOfItem())
                    .address(address)
                    .product(product)
                    .build();

            Transaction transaction = Transaction.builder()
                    .quantity(productRequest.getNumberOfItem())
                    .timestamp(Instant.now())
                    .employee("B11111")
                    .item(item)
                    .build();

            List<Transaction> transactionList = new ArrayList<>();
            transactionList.add(transaction);

            item.setTransactions(transactionList);

            List<Item> itemList = new ArrayList<>();
            itemList.add(item);

            product.setItems(itemList);
        }
        product.setCategories(categories);

        productRepository.save(product);
    }

    // DTO converters:

    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .categories(categoryToDtoList(product.getCategories()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .quantity(productQuantity(product))
                .build();
    }

    static ProductDto productItemToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .quantity(productQuantity(product))
                .build();
    }

    static ProductDto productCategoryToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .categories(categoryToDtoList(product.getCategories()))
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .quantity(productQuantity(product))
                .build();
    }

    static ProductDto productNoCategoryToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .productMeta(productMetaToDtoList(product.getProductMeta()))
                .quantity(productQuantity(product))
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

    /**
     * Function that counts the quantity of the product.
     * This is used to complement the ProductCategoryToDto for example
     * @param product: Product with his associated items
     * @return Integer
     */
    static Integer productQuantity(Product product){
        Integer counter = 0;
        if (product != null && product.getItems() != null){
            for (Item item: product.getItems()){
                counter += item.getQuantity();
            }
        }
        return counter;
    }
}
