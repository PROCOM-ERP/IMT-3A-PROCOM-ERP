package com.example.directoryservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "login_profiles", schema = "public")
public class LoginProfile {
    @Id
    @Size(max = 6)
    @Column(name = "id", nullable = false, length = 6)
    private String id;

    @Builder.Default
    @NotNull
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

    @Builder.Default
    @NotNull
    @Column(name = "jwt_gen_min_at", nullable = false)
    private Instant jwtGenMinAt = Instant.now();

}