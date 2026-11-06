package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidPseudonymException;

import java.util.Objects;

public final class Pseudonym {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    private final String value;

    public Pseudonym(String value) {
        if (value == null || !isValid(value)) {
            throw new InvalidPseudonymException(value);
        }
        this.value = value;
    }

    private boolean isValid(String value) {
        return value.length() >= MIN_LENGTH && value.length() <= MAX_LENGTH;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Pseudonym pseudonym)) return false;
        return value.equals(pseudonym.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
