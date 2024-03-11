package com.example.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees", schema = "public")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_id_gen")
    @SequenceGenerator(name = "employees_id_gen", sequenceName = "employees_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "login_profile", nullable = false)
    private LoginProfile loginProfile;

    @Size(max = 255)
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 255)
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Size(max = 320)
    @NotNull
    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Builder.Default
    @Size(max = 24)
    @Column(name = "phone_number", nullable = false, length = 24)
    private String phoneNumber = null;

    @Builder.Default
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}