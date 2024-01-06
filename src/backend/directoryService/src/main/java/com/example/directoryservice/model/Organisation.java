package com.example.directoryservice.model;

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
        @Index(name = "organisations_name_key", columnList = "name", unique = true),
        @Index(name = "organisations_address_key", columnList = "address", unique = true)
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

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "address", nullable = false)
    private Address address;

    @Builder.Default
    @OneToMany(mappedBy = "organisation")
    private Set<Service> services = new LinkedHashSet<>();

}