package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attribute_names")
public class AttributeName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "string_value", nullable = false)
    private String stringValue;

    @NotNull
    @Column(name = "int_value", nullable = false)
    private Integer intValue;

    @NotNull
    @Column(name = "local_date_value", nullable = false, length = Integer.MAX_VALUE)
    private String localDateValue;

    @Size(max = 255)
    @NotNull
    @Column(name = "text_value", nullable = false)
    private String textValue;

    @NotNull
    @JoinColumn(name = "inventory_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Inventory inventory;
}