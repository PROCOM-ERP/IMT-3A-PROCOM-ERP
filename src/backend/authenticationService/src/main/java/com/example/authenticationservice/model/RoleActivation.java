package com.example.authenticationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "role_activations", schema = "public", indexes = {
        @Index(name = "uq_role_activations_role_microservice", columnList = "role, microservice", unique = true)
})
public class RoleActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_activations_id_gen")
    @SequenceGenerator(name = "role_activations_id_gen", sequenceName = "role_activations_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @Size(max = 32)
    @NotNull
    @Column(name = "microservice", nullable = false, length = 32)
    private String microservice;

    @Builder.Default
    @NotNull
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = false;

}