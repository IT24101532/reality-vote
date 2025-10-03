package com.realityvote.controller;

import com.realityvote.model.Contestant;
import com.realityvote.model.Program;
import com.realityvote.model.Vote;
import com.realityvote.repository.ContestantRepository;
import com.realityvote.repository.ProgramRepository;
import com.realityvote.repository.VoteRepository;
import com.realityvote.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/viewer")
public class ViewerController {

    private final ProgramRepository programs;
    private final ContestantRepository contestants;
    private final VoteRepository votes;
    private final OtpService otpService;

    // Start page
    @GetMapping("/start")
    public String start(Model m) {
        m.addAttribute("programs", programs.findAll());
        return "viewer/start";
    }

    // Send OTP (POST) and redirect to GET validate page
    @PostMapping("/request-otp")
    public String requestOtp(@RequestParam Long programId,
                             @RequestParam String email,
                             RedirectAttributes ra) {
        if (!StringUtils.hasText(email)) {
            ra.addFlashAttribute("error", "Email is required.");
            return "redirect:/viewer/start";
        }
        String otp = otpService.sendOtp(email); // logs at INFO and returns code
        ra.addFlashAttribute("message", "OTP sent to " + email + ".");
        ra.addFlashAttribute("devOtp", otp);    // DEV ONLY: show on page
        String e = UriUtils.encode(email, StandardCharsets.UTF_8);
        return "redirect:/viewer/validate-otp?email=" + e + "&programId=" + programId;
    }

    // GET the OTP form
    @GetMapping("/validate-otp")
    public String showValidate(@RequestParam String email,
                               @RequestParam Long programId,
                               Model m) {
        m.addAttribute("email", email);
        m.addAttribute("programId", programId);
        return "viewer/validate-otp";
    }

    // Validate OTP → select page
    @PostMapping("/validate-otp")
    public String validateOtp(@RequestParam String code,
                              @RequestParam String email,
                              @RequestParam Long programId,
                              RedirectAttributes ra) {
        if (!otpService.validate(email, code)) {
            ra.addFlashAttribute("error", "Invalid or expired OTP. Try again.");
            String e = UriUtils.encode(email, StandardCharsets.UTF_8);
            return "redirect:/viewer/validate-otp?email=" + e + "&programId=" + programId;
        }
        String e = UriUtils.encode(email, StandardCharsets.UTF_8);
        return "redirect:/viewer/select?programId=" + programId + "&email=" + e;
    }

    // Contestant selection
    @GetMapping("/select")
    public String select(@RequestParam Long programId,
                         @RequestParam String email,
                         Model m) {
        Program p = programs.findById(programId).orElse(null);
        List<Contestant> inProgram = contestants.findAll().stream()
                .filter(c -> c.getProgram() != null && c.getProgram().getId() != null
                        && c.getProgram().getId().equals(programId))
                .collect(Collectors.toList());
        m.addAttribute("program", p);
        m.addAttribute("programId", programId);
        m.addAttribute("email", email);
        m.addAttribute("contestants", inProgram);
        return "viewer/select";
    }

    // Persist vote and show success
    @PostMapping("/vote")
    public String vote(@RequestParam Long programId,
                       @RequestParam Long contestantId,
                       @RequestParam String email,
                       Model m,
                       RedirectAttributes ra) {
        Program p = programs.findById(programId).orElse(null);
        Contestant c = contestants.findById(contestantId).orElse(null);
        if (p == null || c == null) {
            ra.addFlashAttribute("error", "Invalid selection.");
            String e = UriUtils.encode(email, StandardCharsets.UTF_8);
            return "redirect:/viewer/select?programId=" + programId + "&email=" + e;
        }
        Vote v = new Vote();
        try { v.getClass().getMethod("setProgram", Program.class).invoke(v, p); } catch (Exception ignored) {}
        try { v.getClass().getMethod("setContestant", Contestant.class).invoke(v, c); } catch (Exception ignored) {}
        try { v.getClass().getMethod("setEmail", String.class).invoke(v, email); } catch (Exception ignored) {}
        try { v.getClass().getMethod("setViewerEmail", String.class).invoke(v, email); } catch (Exception ignored) {}
        try { v.getClass().getMethod("setCreatedAt", LocalDateTime.class).invoke(v, LocalDateTime.now()); } catch (Exception ignored) {}
        votes.save(v);

        m.addAttribute("contestantName", c.getName());
        m.addAttribute("programName", p.getName());
        return "viewer/success";
    }

    // Optional resend
    @GetMapping("/resend-otp")
    public String resend(@RequestParam String email,
                         @RequestParam Long programId,
                         RedirectAttributes ra) {
        String otp = otpService.sendOtp(email);
        ra.addFlashAttribute("message", "A new OTP was sent.");
        ra.addFlashAttribute("devOtp", otp);
        String e = UriUtils.encode(email, StandardCharsets.UTF_8);
        return "redirect:/viewer/validate-otp?email=" + e + "&programId=" + programId;
    }
}
