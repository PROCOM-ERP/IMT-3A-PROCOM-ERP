package com.example.directoryservice.model;

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
@Table(name = "addresses", schema = "public")
public class Address {
    @Id
    @Size(max = 64)
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @NotNull
    @Column(name = "number", nullable = false)
    private Integer number;

    @Size(max = 255)
    @NotNull
    @Column(name = "street", nullable = false)
    private String street;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Builder.Default
    @Size(max = 100)
    @Column(name = "state", length = 100)
    private String state = null;

    @Size(max = 100)
    @NotNull
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Size(max = 20)
    @NotNull
    @Column(name = "zipcode", nullable = false, length = 20)
    private String zipcode;

    @Builder.Default
    @Column(name = "info", length = Integer.MAX_VALUE)
    private String info = null;

    /* No use for orgUnits and organisations fields
    @OneToMany(mappedBy = "address")
    private Set<OrgUnit> orgUnits = new LinkedHashSet<>();

    @OneToMany(mappedBy = "address")
    private Set<Organisation> organisations = new LinkedHashSet<>();
     */

}