package com.example.authenticationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "role_services", schema = "public")
public class RoleServices {
    @EmbeddedId
    private RoleServiceId id;

    @MapsId("role")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @NotNull
    @Column(name = "service", nullable = false)
    private String service;

    @NotNull
    @Column(name = "enable", nullable = false)
    private Boolean enable = true;

}