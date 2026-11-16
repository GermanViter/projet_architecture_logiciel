package com.rpg.domain.lobby.exceptions;

public final class CannotRemoveAnotherPlayerException extends RuntimeException {
    public CannotRemoveAnotherPlayerException() {
        super("A player can only remove themselves from a lobby.");
    }
}
