package com.example.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders", schema = "public")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_id_gen")
    @SequenceGenerator(name = "orders_id_gen", sequenceName = "orders_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Size(max = 64)
    @NotNull
    @Column(name = "quote", nullable = false, length = 64)
    private String quote;

    @Builder.Default
    @NotNull
    @Column(name = "progress_status",nullable = false)
    private Integer progressStatus = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "address", nullable = false)
    private Address address;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "provider", nullable = false)
    private Provider provider;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orderer", nullable = false)
    private Employee orderer;

    @Builder.Default
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver")
    private Employee approver = null;

    @Builder.Default
    @NotNull
    @JsonIgnoreProperties("order")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private Set<OrderProduct> orderProducts = new LinkedHashSet<>();

}