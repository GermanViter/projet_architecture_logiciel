package com.rpg.domain.lobby.exceptions;

public final class CannotRemoveAnotherPlayerException extends LobbyException {
    public CannotRemoveAnotherPlayerException() {
        super("FORBIDDEN", "You can only remove yourself from a lobby.");
    }
}
