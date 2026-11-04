package com.rpg.domain.player;

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

    public String getHash() {
        return hash;
    }
}
