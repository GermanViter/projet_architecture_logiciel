package com.rpg.domain.lobby.exceptions;

public final class LobbyFullException extends RuntimeException {
    public LobbyFullException() {
        super("Lobby is full.");
    }
}
