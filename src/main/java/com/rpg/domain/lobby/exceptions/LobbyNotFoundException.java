package com.rpg.domain.lobby.exceptions;

public final class LobbyNotFoundException extends LobbyException {
    public LobbyNotFoundException() {
        super("LOBBY_NOT_FOUND", "This lobby does not exist.");
    }
}
