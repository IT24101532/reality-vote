package com.realityvote.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name="uk_vote_viewer_program", columnNames={"viewer_id","program_id"}))
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "viewer_id")
    private UserAccount viewer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(optional = false)
    private Contestant contestant;

    private LocalDateTime votedAt;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserAccount getViewer() { return viewer; }
    public void setViewer(UserAccount viewer) { this.viewer = viewer; }
    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }
    public Contestant getContestant() { return contestant; }
    public void setContestant(Contestant contestant) { this.contestant = contestant; }
}
