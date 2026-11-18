package com.rpg.domain.lobby.exceptions;

public final class PlayersNotReadyException extends LobbyException {
    public PlayersNotReadyException() {
        super("PLAYERS_NOT_READY", "Every player must select a hero before the game can start.");
    }
}
