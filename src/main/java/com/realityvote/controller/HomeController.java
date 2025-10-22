package com.realityvote.controller;

import com.realityvote.model.FaqQuery;
import com.realityvote.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final AdvertisementRepository adRepo;
    private final FaqRepository faqRepo;
    private final WhatsNewRepository whatsNewRepo;
    private final FaqQueryRepository faqQueryRepo;

    public HomeController(AdvertisementRepository adRepo,
                          FaqRepository faqRepo,
                          WhatsNewRepository whatsNewRepo,
                          FaqQueryRepository faqQueryRepo) {
        this.adRepo = adRepo;
        this.faqRepo = faqRepo;
        this.whatsNewRepo = whatsNewRepo;
        this.faqQueryRepo = faqQueryRepo;
    }

    // 🏠 Homepage
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("ads", adRepo.findAll());
        model.addAttribute("faqs", faqRepo.findAll());
        model.addAttribute("query", new FaqQuery());
        return "index";
    }

    // 🔑 Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 📰 What's New Page
    @GetMapping("/whats-new")
    public String whatsNew(Model model) {
        model.addAttribute("posts", whatsNewRepo.findAll());
        return "whats-new";
    }

    // 💬 Save FAQ Query (Viewer Form)
    @PostMapping("/faq/query/save")
    public String saveQuery(@ModelAttribute FaqQuery query, Model model) {
        try {
            faqQueryRepo.save(query);
            model.addAttribute("message", "✅ Thank you! We’ll get back to you soon.");
        } catch (Exception e) {
            model.addAttribute("error", "❌ Something went wrong. Please try again.");
        }
        model.addAttribute("ads", adRepo.findAll());
        model.addAttribute("faqs", faqRepo.findAll());
        model.addAttribute("query", new FaqQuery());
        return "index";
    }

    // 🚀 Post-Login Redirect Logic
    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        if (auth == null) return "redirect:/login";

        String roles = auth.getAuthorities().toString();
        if (roles.contains("ADMIN")) return "redirect:/admin/dashboard";
        if (roles.contains("CONTESTANT")) return "redirect:/contestant/dashboard";
        if (roles.contains("VIEWER")) return "redirect:/viewer/select";

        return "redirect:/";
    }
}
