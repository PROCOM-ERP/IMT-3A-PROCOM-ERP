package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @JoinColumn(name = "inventory_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Inventory inventory;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "alias")
    private String alias;

    @Size(max = 255)
    @Column(name = "status")
    private String status;

    @NotNull
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "removal_date")
    private LocalDate removalDate;

}