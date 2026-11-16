package com.rpg.domain.lobby.exceptions;

public final class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException() {
        super("Lobby not found.");
    }
}
