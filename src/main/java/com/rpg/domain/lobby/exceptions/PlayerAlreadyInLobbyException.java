package com.rpg.domain.lobby.exceptions;

public final class PlayerAlreadyInLobbyException extends LobbyException {
    public PlayerAlreadyInLobbyException() {
        super("ALREADY_IN_LOBBY", "Player is already in an open lobby.");
    }
}
