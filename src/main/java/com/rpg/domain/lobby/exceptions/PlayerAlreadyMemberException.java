package com.rpg.domain.lobby.exceptions;

public final class PlayerAlreadyMemberException extends LobbyException {
    public PlayerAlreadyMemberException() {
        super("ALREADY_MEMBER", "Vous êtes déjà membre de ce lobby.");
    }
}
