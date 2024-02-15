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
@Table(name = "organisations", schema = "public", indexes = {
        @Index(name = "organisations_name_key", columnList = "name", unique = true)
})
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisations_id_gen")
    @SequenceGenerator(name = "organisations_id_gen", sequenceName = "organisations_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "address")
    private Address address = null;

    @Builder.Default
    @JsonIgnoreProperties(value = {"organisation"})
    @OneToMany(mappedBy = "organisation")
    private Set<OrgUnit> orgUnits = new LinkedHashSet<>();

}