package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidPasswordException;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class Password {
    private static final int MIN_LENGTH = 8;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BYTES = 32;
    private static final int MEMORY_COST_KB = 19_456;
    private static final int ITERATIONS = 2;
    private static final int PARALLELISM = 1;
    private static final SecureRandom SALT_SOURCE = new SecureRandom();

    private final byte[] salt;
    private final byte[] hash;

    public Password(String plaintext) {
        if (plaintext == null || plaintext.length() < MIN_LENGTH) {
            throw new InvalidPasswordException();
        }
        this.salt = newSalt();
        this.hash = argon2id(plaintext, salt);
    }

    private static byte[] newSalt() {
        byte[] newSalt = new byte[SALT_LENGTH_BYTES];
        SALT_SOURCE.nextBytes(newSalt);
        return newSalt;
    }

    private static byte[] argon2id(String plaintext, byte[] salt) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withSalt(salt)
                .withMemoryAsKB(MEMORY_COST_KB)
                .withIterations(ITERATIONS)
                .withParallelism(PARALLELISM)
                .build());
        byte[] derived = new byte[HASH_LENGTH_BYTES];
        generator.generateBytes(plaintext.getBytes(StandardCharsets.UTF_8), derived);
        return derived;
    }

    public boolean verify(String plaintext) {
        if (plaintext == null) return false;
        return MessageDigest.isEqual(hash, argon2id(plaintext, salt));
    }

    public String getHash() {
        return HexFormat.of().formatHex(hash);
    }
}
