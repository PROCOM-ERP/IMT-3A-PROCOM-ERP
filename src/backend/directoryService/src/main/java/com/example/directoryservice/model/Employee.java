package com.example.directoryservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees", schema = "public", indexes = {
        @Index(name = "employees_email_key", columnList = "email", unique = true)
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Employee {
    @Id
    @Size(max = 6)
    @Column(name = "id", nullable = false, length = 6)
    private String id;

    @Builder.Default
    @NotNull
    @Column(name = "creation", nullable = false)
    private LocalDate creation = LocalDate.now();

    @Builder.Default
    @NotNull
    @Column(name = "enable", nullable = false)
    private Boolean enable = true;

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

    @JsonIgnoreProperties(value = {"organisation", "address", "employees"})
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "service")
    private Service service;

}