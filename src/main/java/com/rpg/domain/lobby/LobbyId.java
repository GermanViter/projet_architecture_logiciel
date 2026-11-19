package com.rpg.domain.lobby;

import java.util.Objects;
import java.util.UUID;

public final class LobbyId {
    private final String value;

    private LobbyId(String value) {
        this.value = value;
    }

    public static LobbyId generate() {
        return new LobbyId(UUID.randomUUID().toString());
    }

    public static LobbyId of(String value) {
        return new LobbyId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof LobbyId lobbyId)) return false;
        return value.equals(lobbyId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
