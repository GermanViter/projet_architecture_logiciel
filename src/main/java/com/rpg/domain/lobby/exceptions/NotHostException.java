package com.rpg.domain.lobby.exceptions;

public final class NotHostException extends LobbyException {
    public NotHostException() {
        super("NOT_HOST", "Only the host can start the game.");
    }
}
