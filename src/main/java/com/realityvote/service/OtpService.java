package com.realityvote.service;

import com.realityvote.model.OtpToken;
import com.realityvote.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpTokenRepository otpRepo;
    private final Random random = new Random();

    public String generateAndStore(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        OtpToken token = OtpToken.builder()
                .email(email)
                .code(code)
                .expiry(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        otpRepo.save(token);
        System.out.println("DEBUG OTP for " + email + " = " + code);
        return code;
    }

    public boolean validate(String email, String code) {
        return otpRepo.findTopByEmailOrderByExpiryDesc(email)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .filter(t -> t.getCode().equals(code))
                .map(t -> { t.setUsed(true); otpRepo.save(t); return true; })
                .orElse(false);
    }
}
