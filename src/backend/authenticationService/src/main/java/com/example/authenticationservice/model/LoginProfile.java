package com.example.authenticationservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "login_profiles", schema = "public", indexes = {
        @Index(name = "login_profiles_id_login_profile_gen_key", columnList = "id_login_profile_gen", unique = true),
        @Index(name = "login_profiles_email_key", columnList = "email", unique = true)
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class LoginProfile {
    @Id
    @Size(max = 6)
    @Column(name = "id", nullable = false, length = 6)
    private String id;

    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "login_profiles_id_gen")
    @SequenceGenerator(name = "login_profiles_id_gen",
            sequenceName = "login_profiles_id_login_profile_gen_seq",
            allocationSize = 1)
    @Column(name = "id_login_profile_gen", nullable = false)
    private Integer idLoginProfileGen;

    @Size(max = 128)
    @NotNull
    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Size(max = 320)
    @Column(name = "email", length = 320)
    private String email;

    @Builder.Default
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @NotNull
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

    @Builder.Default
    @NotNull
    @Column(name = "jwt_gen_min_at", nullable = false)
    private Instant jwtGenMinAt = Instant.now();

    @Builder.Default
    @JsonIgnoreProperties(value = {"login_profiles", "permissions", "is_enable"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "join_login_profiles_roles",
            joinColumns = @JoinColumn(name = "login_profile"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles = new LinkedHashSet<>();

}