package com.rpg.domain.lobby.exceptions;

public final class NotEnoughPlayersException extends LobbyException {
    public NotEnoughPlayersException() {
        super("NOT_ENOUGH_PLAYERS", "Not enough players to start the game.");
    }
}
