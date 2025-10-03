package com.realityvote.service;

import com.realityvote.model.Program;
import com.realityvote.model.Contestant;
import com.realityvote.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {
    private final ProgramRepository programs;
    private final ContestantRepository contestants;

    @Override
    public void run(String... args) {
        System.out.println("SEED: InitData running...");
        if (programs.findAll().isEmpty()) {
            var p = programs.save(Program.builder().name("Super Singer").description("Season 1").build());
            contestants.save(Contestant.builder()
                    .name("Alice")
                    .bio("Singer")
                    .program(p)
                    .email("alice@rv.com") // matches the in-memory contestant user
                    .build());
        }
    }
}
