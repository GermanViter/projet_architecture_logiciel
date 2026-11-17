package com.rpg.domain.lobby.exceptions;

public final class GameAlreadyStartedException extends LobbyException {
    public GameAlreadyStartedException() {
        super("GAME_ALREADY_STARTED", "The game has already started.");
    }
}
