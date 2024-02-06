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
@Table(name = "role_activations", schema = "public")
public class RoleActivation {
    @EmbeddedId
    private RoleActivationId id;

    @MapsId("role")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @NotNull
    @Size(max = 32)
    @Column(name = "microservice", nullable = false)
    private String microservice;

    @Builder.Default
    @NotNull
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

}