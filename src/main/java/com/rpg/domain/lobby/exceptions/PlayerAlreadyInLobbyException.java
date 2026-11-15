package com.rpg.domain.lobby.exceptions;

public final class PlayerAlreadyInLobbyException extends RuntimeException {
    public PlayerAlreadyInLobbyException() {
        super("Player is already in an open lobby.");
    }
}
