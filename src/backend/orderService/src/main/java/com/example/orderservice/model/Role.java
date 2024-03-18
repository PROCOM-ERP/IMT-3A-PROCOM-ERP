package com.example.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role"))
    @Column(name = "permission")
    private Set<String> permissions = new LinkedHashSet<>();

}