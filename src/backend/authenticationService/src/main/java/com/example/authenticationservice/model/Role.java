package com.example.authenticationservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "roles", schema = "public")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class Role {
    @Id
    @Size(max = 32)
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Builder.Default
    @NotNull
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = false;

    @Builder.Default
    @NotNull
    @JsonIgnoreProperties("role")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role", cascade = CascadeType.ALL)
    private Set<RoleActivation> roleActivations = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @JsonIgnoreProperties("roles")
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<LoginProfile> loginProfiles = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role"))
    @Column(name = "permission")
    private Set<String> permissions = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}