package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category_product")
public class CategoryProduct {
    @EmbeddedId
    private CategoryProductId id;

    @MapsId("idCategory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_category", nullable = false)
    private Category idCategory;

}