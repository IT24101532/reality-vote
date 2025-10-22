package com.realityvote.controller;

import com.realityvote.model.UserAccount;
import com.realityvote.model.enums.Role;
import com.realityvote.repository.UserAccountRepository;
import com.realityvote.service.OtpService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Controller
@RequestMapping("/viewer")
public class AuthController {

    private final UserAccountRepository userRepo;
    private final BCryptPasswordEncoder enc;
    private final OtpService otpService;

    public AuthController(UserAccountRepository userRepo, BCryptPasswordEncoder enc, OtpService otpService) {
        this.userRepo = userRepo;
        this.enc = enc;
        this.otpService = otpService;
    }

    // ---------- Inner DTO ----------
    public static class RegisterForm {
        @NotBlank private String username;
        @NotBlank private String password;
        @NotBlank private String fullName;
        @NotBlank @Email private String email;
        private String phone;

        // Getters & Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    // ---------- GET: Registration Page ----------
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new RegisterForm());
        return "viewer/register";
    }

    // ---------- POST: Handle Registration ----------
    @PostMapping("/register/save")
    public String saveRegister(@ModelAttribute("user") RegisterForm form, Model model) {

        if (userRepo.findByUsername(form.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "viewer/register";
        }

        if (userRepo.findByEmail(form.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already used!");
            return "viewer/register";
        }

        // Create and save user
        UserAccount u = new UserAccount();
        u.setUsername(form.getUsername());
        u.setPasswordHash(enc.encode(form.getPassword()));
        u.setFullName(form.getFullName());
        u.setEmail(form.getEmail());
        u.setPhone(form.getPhone());
        u.setRole(Role.VIEWER);
        u.setEnabled(false);
        userRepo.save(u);

        // Send OTP and redirect to verify
        otpService.sendOtp(form.getEmail());
        model.addAttribute("email", form.getEmail());
        return "viewer/verify"; // ✅ FIXED PATH
    }

    // ---------- POST: Verify OTP ----------
    @PostMapping("/otp/verify")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("code") String code,
                            Model model) {

        boolean ok = otpService.validate(email, code);

        if (!ok) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            return "viewer/validate-otp"; // ✅ Your existing OTP page
        }

        UserAccount u = userRepo.findByEmail(email).orElseThrow();
        u.setEnabled(true);
        userRepo.save(u);

        model.addAttribute("fullName", u.getFullName());
        model.addAttribute("email", u.getEmail());
        return "viewer/success"; // ✅ Show success page (don’t redirect)
    }



    // ---------- GET: Login ----------
    @GetMapping("/login")
    public String viewerLogin() {
        return "viewer/login";
    }

    // ---------- GET: Viewer Start ----------
    @GetMapping("/start")
    public String viewerStart() {
        return "viewer/start";
    }
}
