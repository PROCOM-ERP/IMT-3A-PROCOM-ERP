package com.example.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class RoleServiceId implements Serializable {
    private static final long serialVersionUID = -5923163786474763967L;
    @Size(max = 32)
    @NotNull
    @Column(name = "role", nullable = false, length = 32)
    private String role;

    @Size(max = 32)
    @NotNull
    @Column(name = "service", nullable = false, length = 32)
    private String service;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleServiceId entity = (RoleServiceId) o;
        return Objects.equals(this.role, entity.role) &&
                Objects.equals(this.service, entity.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, service);
    }

}