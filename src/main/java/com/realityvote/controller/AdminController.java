package com.realityvote.controller;

import com.realityvote.model.Advertisement;
import com.realityvote.model.Faq;
import com.realityvote.model.Program;
import com.realityvote.model.Contestant;
import com.realityvote.repository.AdvertisementRepository;
import com.realityvote.repository.ContestantRepository;
import com.realityvote.repository.FaqRepository;
import com.realityvote.repository.ProgramRepository;
import com.realityvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProgramRepository programs;
    private final ContestantRepository contestants;
    private final FaqRepository faqs;
    private final AdvertisementRepository ads;
    private final VoteRepository votes;

    // ---- Programs ----
    @GetMapping("/programs")
    public String programs(Model m) {
        m.addAttribute("programs", programs.findAll());
        m.addAttribute("program", new Program());
        return "admin/programs";
    }

    @PostMapping("/programs")
    public String saveProgram(@ModelAttribute @Validated Program p) {
        programs.save(p);
        return "redirect:/admin/programs";
    }

    @GetMapping("/programs/delete/{id}")
    public String delProgram(@PathVariable Long id) {
        programs.deleteById(id);
        return "redirect:/admin/programs";
    }

    // ---- Contestants ----
    @GetMapping("/contestants")
    public String contestants(Model m) {
        m.addAttribute("contestants", contestants.findAll());
        m.addAttribute("programs", programs.findAll());
        m.addAttribute("contestant", new Contestant());
        return "admin/contestants";
    }

    @PostMapping("/contestants")
    public String saveContestant(@ModelAttribute Contestant c,
                                 @RequestParam("programId") Long programId) {
        c.setProgram(programs.findById(programId).orElseThrow());
        contestants.save(c);
        return "redirect:/admin/contestants";
    }

    @GetMapping("/contestants/delete/{id}")
    public String delContestant(@PathVariable Long id) {
        contestants.deleteById(id);
        return "redirect:/admin/contestants";
    }

    // ---- FAQs ----
    @GetMapping("/faqs")
    public String faqs(Model m) {
        m.addAttribute("faqs", faqs.findAll());
        m.addAttribute("faq", new Faq());
        return "admin/faqs";
    }

    @PostMapping("/faqs")
    public String saveFaq(@ModelAttribute Faq f) {
        faqs.save(f);
        return "redirect:/admin/faqs";
    }

    @GetMapping("/faqs/delete/{id}")
    public String delFaq(@PathVariable Long id) {
        faqs.deleteById(id);
        return "redirect:/admin/faqs";
    }

    // ---- Advertisements ----
    @GetMapping("/ads")
    public String ads(Model m) {
        m.addAttribute("ads", ads.findAll());
        m.addAttribute("ad", new Advertisement());
        return "admin/ads";
    }

    @PostMapping("/ads")
    public String saveAd(@ModelAttribute Advertisement a) {
        ads.save(a);
        return "redirect:/admin/ads";
    }

    @GetMapping("/ads/delete/{id}")
    public String delAd(@PathVariable Long id) {
        ads.deleteById(id);
        return "redirect:/admin/ads";
    }

    // ---- Reports ----
    @GetMapping("/reports/{programId}")
    public String report(@PathVariable Long programId, Model m) {
        m.addAttribute("program", programs.findById(programId).orElse(null));
        m.addAttribute("rows", votes.tallyByProgram(programId));
        return "admin/report";
    }
}
