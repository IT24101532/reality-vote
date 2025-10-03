package com.realityvote.controller;

import com.realityvote.model.Contestant;
import com.realityvote.repository.ContestantRepository;
import com.realityvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contestant")
public class ContestantController {

    private final ContestantRepository contestants;
    private final VoteRepository votes;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model m){
        Contestant c = contestants.findByEmail(user.getUsername()).orElse(null);
        long count = (c == null) ? 0 : votes.countByContestant(c);
        m.addAttribute("contestant", c);
        m.addAttribute("votes", count);
        return "contestant/dashboard";
    }

    // Upload a new photo file
    @PostMapping("/photo/upload")
    public String uploadPhoto(@AuthenticationPrincipal User user,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes ra) throws IOException {
        var c = contestants.findByEmail(user.getUsername()).orElse(null);
        if (c == null || file == null || file.isEmpty()) {
            ra.addFlashAttribute("msgError", "Please choose an image file.");
            return "redirect:/contestant/dashboard";
        }

        Path folder = Path.of("uploads", "contestants");
        Files.createDirectories(folder);

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = "c_" + c.getId() + "_" + System.currentTimeMillis()
                + (StringUtils.hasText(ext) ? "." + ext.toLowerCase() : "");
        Path target = folder.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Public URL via /uploads/** mapping
        c.setPhoto("/uploads/contestants/" + filename);
        contestants.save(c);

        ra.addFlashAttribute("msgOk", "Profile photo updated.");
        return "redirect:/contestant/dashboard";
    }

    // Set photo by external URL
    @PostMapping("/photo/url")
    public String setPhotoUrl(@AuthenticationPrincipal User user,
                              @RequestParam("imageUrl") String url,
                              RedirectAttributes ra) {
        var c = contestants.findByEmail(user.getUsername()).orElse(null);
        if (c == null || !StringUtils.hasText(url)) {
            ra.addFlashAttribute("msgError", "Please paste a valid image URL.");
            return "redirect:/contestant/dashboard";
        }
        c.setPhoto(url.trim());
        contestants.save(c);
        ra.addFlashAttribute("msgOk", "Profile photo updated.");
        return "redirect:/contestant/dashboard";
    }
}
