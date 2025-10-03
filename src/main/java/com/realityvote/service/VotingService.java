package com.realityvote.service;

import com.realityvote.model.*;
import com.realityvote.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final VoteRepository votes;
    private final ProgramRepository programs;
    private final ContestantRepository contestants;

    public List<Program> allPrograms() { return programs.findAll(); }
    public List<Contestant> contestantsOf(Long programId) {
        return programs.findById(programId).map(contestants::findByProgram).orElse(List.of());
    }

    public void recordVote(String email, Long programId, Long contestantId) {
        Program p = programs.findById(programId).orElseThrow();
        Contestant c = contestants.findById(contestantId).orElseThrow();
        Vote v = Vote.builder().email(email).program(p).contestant(c).timestamp(LocalDateTime.now()).build();
        votes.save(v);
    }
}
