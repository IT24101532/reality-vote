package com.realityvote.service;

import com.realityvote.model.*;
import com.realityvote.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VotingService {
    private final VoteRepository voteRepo;
    private final ContestantRepository contestantRepo;

    public VotingService(VoteRepository voteRepo, ContestantRepository contestantRepo) {
        this.voteRepo = voteRepo;
        this.contestantRepo = contestantRepo;
    }

    @Transactional
    public String castVote(UserAccount viewer, Long contestantId) {
        Contestant c = contestantRepo.findById(contestantId)
                .orElseThrow(() -> new RuntimeException("Contestant not found"));

        // ✅ Contestant may have multiple programs; we’ll pick the first one for voting
        if (c.getPrograms() == null || c.getPrograms().isEmpty()) {
            return "⚠️ This contestant is not assigned to any program!";
        }
        Program program = c.getPrograms().get(0);

        // ✅ Prevent duplicate votes for the same show
        if (voteRepo.findByViewerAndProgram(viewer, program).isPresent()) {
            return "⚠️ You have already voted for this show!";
        }

        // ✅ Save new vote
        Vote v = new Vote();
        v.setViewer(viewer);
        v.setProgram(program);
        v.setContestant(c);
        voteRepo.save(v);

        return "✅ Your vote for " + c.getName() + " in " + program.getName() + " has been recorded!";
    }
}
