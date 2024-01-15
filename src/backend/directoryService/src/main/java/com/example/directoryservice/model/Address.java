package com.example.directoryservice.model;

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
@Entity
@Table(name = "addresses", schema = "public")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_id_gen")
    @SequenceGenerator(name = "addresses_id_gen", sequenceName = "addresses_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Builder.Default
    @Column(name = "number")
    private Integer number = null;

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
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Builder.Default
    @Column(name = "info", length = Integer.MAX_VALUE)
    private String info = null;

    @Builder.Default
    @OneToOne(mappedBy = "address")
    private Organisation organisation = null;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "services", joinColumns = @JoinColumn(name = "address"))
    @Column(name = "id")
    private Set<Integer> services = new LinkedHashSet<>();

}