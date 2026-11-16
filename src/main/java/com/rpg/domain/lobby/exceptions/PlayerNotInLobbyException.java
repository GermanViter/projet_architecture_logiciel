package com.rpg.domain.lobby.exceptions;

public final class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException() {
        super("Player is not a member of this lobby.");
    }
}
