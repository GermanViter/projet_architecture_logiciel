package com.rpg.domain.lobby.exceptions;

public final class CannotRemoveAnotherPlayerException extends LobbyException {
    public CannotRemoveAnotherPlayerException() {
        super("FORBIDDEN", "Vous ne pouvez retirer que vous-même d'un lobby.");
    }
}
