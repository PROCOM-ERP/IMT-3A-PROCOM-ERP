package com.example.directoryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "org_units", schema = "public", indexes = {
        @Index(name = "uq_org_units_name_organisation", columnList = "name, organisation", unique = true)
})
public class OrgUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_units_id_gen")
    @SequenceGenerator(name = "org_units_id_gen", sequenceName = "org_units_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @JsonIgnoreProperties(value = {"orgUnit"})
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "manager")
    private Employee manager = null;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "org_unit")
    private OrgUnit orgUnit = null;

    @NotNull
    @JsonIgnoreProperties(value = {"orgUnits"})
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organisation", nullable = false)
    private Organisation organisation;

    @Builder.Default
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "address")
    private Address address = null;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "org_units",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "org_unit")
    private Set<Integer> orgUnits = new LinkedHashSet<>();

    /* No use for employees field
    @OneToMany(mappedBy = "orgUnit")
    private Set<Employee> employees = new LinkedHashSet<>();
     */

}