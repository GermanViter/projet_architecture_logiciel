package com.rpg.domain.lobby.exceptions;

public final class HeroAlreadySelectedException extends LobbyException {
    public HeroAlreadySelectedException() {
        super("HERO_ALREADY_SELECTED", "This player has already selected a hero.");
    }
}
