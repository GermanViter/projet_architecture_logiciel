package com.rpg.domain.lobby.exceptions;

public final class PlayerAlreadyMemberException extends RuntimeException {
    public PlayerAlreadyMemberException() {
        super("Player is already a member of this lobby.");
    }
}
