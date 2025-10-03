package com.realityvote.controller;

import com.realityvote.repository.AdvertisementRepository;
import com.realityvote.repository.FaqRepository;
import com.realityvote.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProgramRepository programs;
    private final FaqRepository faqs;
    private final AdvertisementRepository ads;

    // Home page
    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("faqs", faqs.findAll());
        m.addAttribute("ads", ads.findAll());
        return "index";
    }

    // Admin dashboard (admins are allowed by Spring Security)
    @GetMapping("/dashboard")
    public String adminDashboard(Model m) {
        m.addAttribute("programs", programs.findAll()); // used by the dropdown hint
        return "dashboard";
    }

    // NOTE: No /default mapping here — AuthController handles role-based redirect.
}
