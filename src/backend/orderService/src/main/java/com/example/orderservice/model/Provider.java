package com.example.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "providers", schema = "public", indexes = {
        @Index(name = "uq_providers_name", columnList = "name", unique = true)
})
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "providers_id_gen")
    @SequenceGenerator(name = "providers_id_gen", sequenceName = "providers_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

}