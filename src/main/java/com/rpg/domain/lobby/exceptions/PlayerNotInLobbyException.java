package com.rpg.domain.lobby.exceptions;

public final class PlayerNotInLobbyException extends LobbyException {
    public PlayerNotInLobbyException() {
        super("PLAYER_NOT_IN_LOBBY", "This player is not a member of this lobby.");
    }
}
