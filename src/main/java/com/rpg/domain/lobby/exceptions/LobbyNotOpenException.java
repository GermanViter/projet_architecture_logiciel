package com.rpg.domain.lobby.exceptions;

public final class LobbyNotOpenException extends LobbyException {
    public LobbyNotOpenException() {
        super("LOBBY_NOT_OPEN", "This lobby is no longer open.");
    }
}
