package com.realityvote.service;

import com.realityvote.model.UserAccount;
import com.realityvote.model.enums.Role;
import com.realityvote.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {
    private final UserAccountRepository repo;
    private final BCryptPasswordEncoder enc;

    public InitData(UserAccountRepository repo, BCryptPasswordEncoder enc) {
        this.repo = repo; this.enc = enc;
    }

    @Override
    public void run(String... args) {
        if (repo.findByUsername("admin").isEmpty()) {
            UserAccount admin = new UserAccount();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(enc.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setFullName("Administrator");
            repo.save(admin);
            System.out.println("Seeded admin / admin123");
        }
    }
}
