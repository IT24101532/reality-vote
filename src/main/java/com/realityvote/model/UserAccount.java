package com.realityvote.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // contestant/admin profile fields
    private String displayName;
    private String phone;
    private boolean enabled = true;

    public enum Role { ROLE_ADMIN, ROLE_CONTESTANT }
}
