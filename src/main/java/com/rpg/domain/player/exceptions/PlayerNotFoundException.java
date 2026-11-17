package com.rpg.domain.player.exceptions;

public class PlayerNotFoundException extends PlayerException {
    public PlayerNotFoundException(String playerId) {
        super("PLAYER_NOT_FOUND", "Player with ID " + playerId + " was not found.");
    }
}
