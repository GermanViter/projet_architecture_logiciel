package com.rpg.domain.player.exceptions;

public final class PlayerNotFoundException extends PlayerException {
    public PlayerNotFoundException() {
        super("PLAYER_NOT_FOUND", "This player does not exist.");
    }
}
