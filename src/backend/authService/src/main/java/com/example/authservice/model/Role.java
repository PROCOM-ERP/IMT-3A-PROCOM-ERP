package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Builder.Default
    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();

}