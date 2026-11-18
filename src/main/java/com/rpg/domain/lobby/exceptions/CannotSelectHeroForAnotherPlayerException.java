package com.rpg.domain.lobby.exceptions;

public final class CannotSelectHeroForAnotherPlayerException extends LobbyException {
    public CannotSelectHeroForAnotherPlayerException() {
        super("FORBIDDEN", "You can only select a hero for yourself.");
    }
}
