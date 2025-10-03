package com.realityvote.controller;

import com.realityvote.service.OtpService;
import com.realityvote.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller @RequiredArgsConstructor
@RequestMapping("/viewer")
public class ViewerController {
    private final VotingService voting;
    private final OtpService otpService;

    @GetMapping("/start")
    public String start(Model m){
        m.addAttribute("programs", voting.allPrograms());
        return "viewer/start";
    }

    @PostMapping("/request-otp")
    public String requestOtp(@RequestParam Long programId, @RequestParam String email, Model m){
        String code = otpService.generateAndStore(email);
        m.addAttribute("email", email);
        m.addAttribute("programId", programId);
        m.addAttribute("info", "OTP sent to your email (for demo check server console).");
        return "viewer/enter-otp";
    }

    @PostMapping("/validate-otp")
    public String validateOtp(@RequestParam Long programId, @RequestParam String email, @RequestParam String code, Model m){
        if (otpService.validate(email, code)) {
            m.addAttribute("email", email);
            m.addAttribute("programId", programId);
            m.addAttribute("contestants", voting.contestantsOf(programId));
            return "viewer/choose";
        } else {
            m.addAttribute("email", email);
            m.addAttribute("programId", programId);
            m.addAttribute("error", "Invalid or expired OTP. Try again.");
            return "viewer/enter-otp";
        }
    }

    @PostMapping("/vote")
    public String vote(@RequestParam Long programId, @RequestParam String email, @RequestParam Long contestantId, Model m){
        voting.recordVote(email, programId, contestantId);
        m.addAttribute("message", "Vote recorded. Thank you!");
        return "viewer/success";
    }
}
