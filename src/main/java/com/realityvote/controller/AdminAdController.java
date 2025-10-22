package com.realityvote.controller;

import com.realityvote.model.Advertisement;
import com.realityvote.repository.AdvertisementRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ads")
public class AdminAdController {

    private final AdvertisementRepository adRepo;

    public AdminAdController(AdvertisementRepository adRepo) {
        this.adRepo = adRepo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", adRepo.findAll());
        model.addAttribute("ad", new Advertisement());
        return "admin/ads";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Advertisement ad) {
        adRepo.save(ad);
        return "redirect:/admin/ads";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Advertisement ad = adRepo.findById(id).orElseThrow(() -> new RuntimeException("Ad not found"));
        model.addAttribute("ad", ad);
        model.addAttribute("items", adRepo.findAll());
        return "admin/ads";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adRepo.deleteById(id);
        return "redirect:/admin/ads";
    }
}
