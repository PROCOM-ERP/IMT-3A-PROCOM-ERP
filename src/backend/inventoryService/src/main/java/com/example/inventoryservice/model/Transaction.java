package com.example.inventoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_id_transaction")
    @SequenceGenerator(name = "transactions_id_transaction", sequenceName = "transactions_id_transaction_seq", allocationSize = 1)
    @Column(name = "id_transaction", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Size(max = 6)
    @NotNull
    @Column(name = "employee", nullable = false, length = 6)
    private String employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;
}
