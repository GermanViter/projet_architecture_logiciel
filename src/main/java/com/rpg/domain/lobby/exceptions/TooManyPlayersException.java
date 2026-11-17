package com.rpg.domain.lobby.exceptions;

public final class TooManyPlayersException extends LobbyException {
    public TooManyPlayersException() {
        super("TOO_MANY_PLAYERS", "Too many players to start the game.");
    }
}
