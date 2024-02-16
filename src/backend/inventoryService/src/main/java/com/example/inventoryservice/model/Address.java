package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address", nullable = false)
    private Integer id;

    @Size(max = 32)
    @Column(name = "number", length = 32)
    private String number;

    @Size(max = 255)
    @Column(name = "street")
    private String street;

    @Size(max = 63)
    @Column(name = "city", length = 63)
    private String city;

    @Size(max = 63)
    @Column(name = "state", length = 63)
    private String state;

    @Size(max = 63)
    @Column(name = "country", length = 63)
    private String country;

    @Size(max = 15)
    @Column(name = "postal_code", length = 15)
    private String postalCode;

    @Column(name = "info", length = Integer.MAX_VALUE)
    private String info;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "address")
    private List<Item> items;
}