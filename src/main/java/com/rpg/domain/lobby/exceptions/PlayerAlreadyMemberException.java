package com.rpg.domain.lobby.exceptions;

public final class PlayerAlreadyMemberException extends LobbyException {
    public PlayerAlreadyMemberException() {
        super("ALREADY_MEMBER", "You are already a member of this lobby.");
    }
}
