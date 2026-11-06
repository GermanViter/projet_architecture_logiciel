package com.rpg.domain.session;

import java.util.Objects;
import java.util.UUID;

public final class SessionToken {
    private final String value;

    public SessionToken() {
        this.value = UUID.randomUUID().toString();
    }

    private SessionToken(String value) {
        this.value = value;
    }

    public static SessionToken of(String value) {
        return new SessionToken(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SessionToken t)) return false;
        return value.equals(t.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
