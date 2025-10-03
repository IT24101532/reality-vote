package com.realityvote.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contestant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String bio;

    @ManyToOne
    private Program program;

    // Used to link in-memory login to a profile
    private String email;

    // Public URL to the contestant photo (either an uploaded file served at /uploads/** or an external URL)
    private String photo;
}
