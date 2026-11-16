package com.rpg.domain.lobby.exceptions;

public final class LobbyFullException extends LobbyException {
    public LobbyFullException() {
        super("LOBBY_FULL", "Le lobby est plein.");
    }
}
