package com.example.authenticationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class RoleActivationId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1977334691863885855L;
    @Size(max = 32)
    @NotNull
    @Column(name = "role", nullable = false, length = 32)
    private String role;

    @Size(max = 32)
    @NotNull
    @Column(name = "microservice", nullable = false, length = 32)
    private String microservice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleActivationId entity = (RoleActivationId) o;
        return Objects.equals(this.role, entity.role) &&
                Objects.equals(this.microservice, entity.microservice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, microservice);
    }

    public static RoleActivationId of(String role, String microservice) {
        return RoleActivationId.builder()
                .role(role)
                .microservice(microservice)
                .build();
    }

}