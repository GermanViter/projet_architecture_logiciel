package com.rpg.domain.lobby.exceptions;

public final class PlayerNotInLobbyException extends LobbyException {
    public PlayerNotInLobbyException() {
        super("PLAYER_NOT_IN_LOBBY", "Ce joueur n'est pas membre de ce lobby.");
    }
}
