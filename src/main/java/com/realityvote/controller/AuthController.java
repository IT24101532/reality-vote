package com.realityvote.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping("/default")
    public String postLoginRedirect(Authentication auth) {
        if (auth != null) {
            if (hasRole(auth, "ROLE_ADMIN")) return "redirect:/dashboard";
            if (hasRole(auth, "ROLE_CONTESTANT")) return "redirect:/contestant/dashboard";
        }
        return "redirect:/";
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
