package com.rpg.domain.player.exceptions;

public final class CannotViewAnotherPlayerProfileException extends PlayerException {
    public CannotViewAnotherPlayerProfileException() {
        super("FORBIDDEN", "You can only view your own profile.");
    }
}
