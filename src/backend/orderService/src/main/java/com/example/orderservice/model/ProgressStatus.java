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
@Table(name = "progress_status", schema = "public", indexes = {
        @Index(name = "uq_progress_status", columnList = "name", unique = true)
})
public class ProgressStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "progress_status_id_gen")
    @SequenceGenerator(name = "progress_status_id_gen", sequenceName = "progress_status_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

}