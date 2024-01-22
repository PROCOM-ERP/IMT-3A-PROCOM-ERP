package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attributes")
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "attribute_name_id", nullable = false)
    private Integer attributeNameId;

    @Size(max = 255)
    @Column(name = "type")
    private String type;

    @Size(max = 255)
    @Column(name = "string_attribute")
    private String stringAttribute;

    @Size(max = 255)
    @Column(name = "int_attribute")
    private String intAttribute;

    @Size(max = 255)
    @Column(name = "local_date_attribute")
    private String localDateAttribute;

    @Size(max = 255)
    @Column(name = "text_attribute")
    private String textAttribute;

}