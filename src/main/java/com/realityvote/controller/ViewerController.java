package com.realityvote.controller;

import com.realityvote.model.Program;
import com.realityvote.model.UserAccount;
import com.realityvote.repository.ContestantRepository;
import com.realityvote.repository.ProgramRepository;
import com.realityvote.repository.UserAccountRepository;
import com.realityvote.service.VotingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/viewer")
public class ViewerController {

    private final ProgramRepository programRepo;
    private final ContestantRepository contestantRepo;
    private final VotingService votingService;
    private final UserAccountRepository userRepo;

    public ViewerController(ProgramRepository programRepo,
                            ContestantRepository contestantRepo,
                            VotingService votingService,
                            UserAccountRepository userRepo) {
        this.programRepo = programRepo;
        this.contestantRepo = contestantRepo;
        this.votingService = votingService;
        this.userRepo = userRepo;
    }

    // ----------------------- Show Available Programs -----------------------
    @GetMapping("/select")
    public String selectProgram(Model model) {
        // ✅ Only show programs that are active
        model.addAttribute("programs", programRepo.findByActiveTrue());
        return "viewer/select";
    }

    // ----------------------- Vote Page -----------------------
    @GetMapping("/vote/{programId}")
    public String votePage(@PathVariable("programId") Long programId, Model model) {
        Program program = programRepo.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        // ✅ Prevent voting if the show is inactive
        if (!program.isActive()) {
            model.addAttribute("errorMessage", "⚠️ Voting for this show is currently closed.");
            return "viewer/vote-closed"; // you can create a small HTML page for this
        }

        model.addAttribute("program", program);
        model.addAttribute("contestants", contestantRepo.findByProgram(program));
        return "viewer/vote";
    }

    // ----------------------- Cast Vote -----------------------
    @PostMapping("/vote")
    public String castVote(@RequestParam("contestantId") Long contestantId,
                           @AuthenticationPrincipal User springUser,
                           Model model) {

        UserAccount viewer = userRepo.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Viewer not found"));

        String msg = votingService.castVote(viewer, contestantId);
        model.addAttribute("message", msg);
        return "viewer/vote-success";
    }
}
