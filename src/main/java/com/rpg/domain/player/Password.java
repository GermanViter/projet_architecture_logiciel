package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidPasswordException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class Password {
    private static final int MIN_LENGTH = 8;

    private final String hash;

    public Password(String plaintext) {
        if (plaintext == null || plaintext.length() < MIN_LENGTH) {
            throw new InvalidPasswordException();
        }
        this.hash = sha256(plaintext);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // ponytail: no salt — BCrypt/Argon2 with per-user salt if real security is needed
    public boolean verify(String plaintext) {
        if (plaintext == null) return false;
        return this.hash.equals(sha256(plaintext));
    }

    public String getHash() {
        return hash;
    }
}
