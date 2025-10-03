package com.realityvote.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {

    private static final long TTL_SECONDS = 300; // 5 minutes
    private final SecureRandom random = new SecureRandom();

    private static final class Entry {
        final String code;
        final Instant expiresAt;
        Entry(String code, Instant expiresAt) { this.code = code; this.expiresAt = expiresAt; }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** Generates and stores OTP, returns it (for dev/demo). */
    public String sendOtp(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        store.put(email, new Entry(code, Instant.now().plusSeconds(TTL_SECONDS)));

        // Log at INFO so it ALWAYS prints in IntelliJ Run console
        log.info("DEV OTP for {} = {}", email, code);
        System.out.println("DEV OTP for " + email + " = " + code); // extra sure

        // In real life, send via email/SMS here
        return code;
    }

    /** Validates and consumes the OTP. */
    public boolean validate(String email, String code) {
        Entry e = store.get(email);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expiresAt)) { store.remove(email); return false; }
        boolean ok = e.code.equals(code);
        if (ok) store.remove(email);
        return ok;
    }

    /** Dev helper to peek current code (not used in prod). */
    public String peek(String email) {
        Entry e = store.get(email);
        return (e == null || Instant.now().isAfter(e.expiresAt)) ? null : e.code;
    }
}
