package com.realityvote.controller;

import com.realityvote.model.UserAccount;
import com.realityvote.repository.ContestantRepository;
import com.realityvote.repository.UserAccountRepository;
import com.realityvote.repository.VoteRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/contestant")
public class ContestantController {
    private final UserAccountRepository userRepo;
    private final ContestantRepository contestantRepo;
    private final VoteRepository voteRepo;

    public ContestantController(UserAccountRepository userRepo, ContestantRepository contestantRepo, VoteRepository voteRepo) {
        this.userRepo = userRepo; this.contestantRepo = contestantRepo; this.voteRepo = voteRepo;
    }

    @GetMapping("/dashboard")
    public String dash(@AuthenticationPrincipal User springUser, Model m) {
        UserAccount u = userRepo.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var c = contestantRepo.findAll().stream()
                .filter(x -> x.getAccount() != null && x.getAccount().getId().equals(u.getId()))
                .findFirst().orElse(null);
        long votes = (c == null) ? 0 : voteRepo.countByContestantId(c.getId());
        m.addAttribute("contestant", c);
        m.addAttribute("totalVotes", votes);
        return "contestant/dashboard";
    }
    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal User springUser, Model model) {
        UserAccount u = userRepo.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var contestant = contestantRepo.findAll().stream()
                .filter(x -> x.getAccount() != null && x.getAccount().getId().equals(u.getId()))
                .findFirst().orElse(null);
        model.addAttribute("contestant", contestant);
        return "contestant/edit-profile";
    }

    @PostMapping("/edit/save")
    public String saveProfile(@AuthenticationPrincipal User springUser,
                              @RequestParam String bio,
                              @RequestParam(required = false) String imageUrl) {
        UserAccount u = userRepo.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var contestant = contestantRepo.findAll().stream()
                .filter(x -> x.getAccount() != null && x.getAccount().getId().equals(u.getId()))
                .findFirst().orElse(null);

        if (contestant != null) {
            contestant.setBio(bio);
            contestant.setImageUrl(imageUrl);
            contestantRepo.save(contestant);
        }
        return "redirect:/contestant/dashboard";
    }


}
