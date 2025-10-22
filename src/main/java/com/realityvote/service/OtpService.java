package com.realityvote.service;

import com.realityvote.model.OtpToken;
import com.realityvote.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class OtpService {
    private final JavaMailSender mailSender;
    private final OtpTokenRepository repo;
    private final int expiryMinutes;
    private final SecureRandom rnd = new SecureRandom();

    public OtpService(JavaMailSender mailSender, OtpTokenRepository repo,
                      @Value("${app.otp.expiry-minutes:10}") int expiryMinutes) {
        this.mailSender = mailSender;
        this.repo = repo;
        this.expiryMinutes = expiryMinutes;
    }

    public void sendOtp(String email) {
        String code = String.format("%06d", rnd.nextInt(1_000_000));
        OtpToken t = new OtpToken();
        t.setEmail(email);
        t.setCode(code);
        t.setExpiresAt(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES));
        repo.save(t);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Your Reality Vote OTP");
        msg.setText("Your OTP is: " + code + "\nThis code expires in " + expiryMinutes + " minutes.");
        mailSender.send(msg);
    }

    public boolean validate(String email, String code) {
        return repo.findTopByEmailAndUsedFalseOrderByIdDesc(email)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .map(t -> { t.setUsed(true); repo.save(t); return true; })
                .orElse(false);
    }
}
