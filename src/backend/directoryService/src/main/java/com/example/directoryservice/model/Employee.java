package com.example.directoryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees", schema = "public", indexes = {
        @Index(name = "uq_employees_email", columnList = "email", unique = true)
})
public class Employee {
    @Id
    @Size(max = 6)
    @Column(name = "id", nullable = false, length = 6)
    private String id;

    @MapsId
    @JsonIgnoreProperties(value = {"employee"})
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
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
    @Column(name = "phone_number", length = 24)
    private String phoneNumber = null;

    @Builder.Default
    @Size(max = 64)
    @Column(name = "job", length = 64)
    private String job = null;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "org_unit", nullable = false)
    private OrgUnit orgUnit;

}