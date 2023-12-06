package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id_user", nullable = false, length = 6)
    private String idUser;

    @Column(name = "index_user", nullable = false)
    private Integer indexUser;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @Column(name = "\"timestamp\"", nullable = false)
    private LocalDate timestamp;

}