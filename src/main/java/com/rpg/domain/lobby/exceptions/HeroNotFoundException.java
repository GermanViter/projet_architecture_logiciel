package com.rpg.domain.lobby.exceptions;

public final class HeroNotFoundException extends LobbyException {
    public HeroNotFoundException() {
        super("HERO_NOT_FOUND", "Hero not found.");
    }
}
