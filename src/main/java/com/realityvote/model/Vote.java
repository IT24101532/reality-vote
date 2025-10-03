package com.realityvote.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = {@Index(columnList = "program_id"), @Index(columnList = "contestant_id"), @Index(columnList = "email")})
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // viewer email

    @ManyToOne
    private Program program;

    @ManyToOne
    private Contestant contestant;

    private LocalDateTime timestamp;
}
