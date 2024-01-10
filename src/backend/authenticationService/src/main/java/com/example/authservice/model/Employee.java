package com.example.authservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees", schema = "public", indexes = {
        @Index(name = "employees_id_employee_gen_key", columnList = "id_employee_gen", unique = true)
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Employee {
    @Id
    @Size(max = 6)
    @Column(name = "id", nullable = false, length = 6)
    private String id;

    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_id_employee_gen")
    @SequenceGenerator(name = "employees_id_employee_gen", sequenceName = "employees_id_employee_gen_seq", allocationSize = 1)
    @Column(name = "id_employee_gen", nullable = false)
    private Integer idEmployeeGen;

    @Size(max = 128)
    @NotNull
    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Builder.Default
    @Size(max = 320)
    @Column(name = "email", nullable = false, length = 320)
    private String email = null;

    @Builder.Default
    @NotNull
    @Column(name = "creation", nullable = false)
    private LocalDateTime creation = LocalDateTime.now();

    @Builder.Default
    @NotNull
    @Column(name = "enable", nullable = false)
    private Boolean enable = true;

    @Builder.Default
    @NotNull
    @Column(name = "jwt_min_creation", nullable = false)
    private LocalDateTime jwtMinCreation = LocalDateTime.now();

    @Builder.Default
    @JsonIgnoreProperties(value = {"employees", "permissions", "enable"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "join_employees_roles",
            joinColumns = @JoinColumn(name = "employee"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles = new LinkedHashSet<>();

}