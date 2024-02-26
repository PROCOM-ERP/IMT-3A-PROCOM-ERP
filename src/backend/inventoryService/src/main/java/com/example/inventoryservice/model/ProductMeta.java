package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_meta")
public class ProductMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productMeta_id_productMeta")
    @SequenceGenerator(name = "productMeta_id_productMeta", sequenceName = "productMeta_id_productMeta_seq", allocationSize = 1)
    @Column(name = "id_product_meta", nullable = false)     
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "key", nullable = false)
    private String key;

    @Size(max = 16)
    @NotNull
    @Column(name = "type", nullable = false, length = 16)
    private String type;

    @NotNull
    @Column(name = "value", nullable = false, length = Integer.MAX_VALUE)
    private String value;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
}
