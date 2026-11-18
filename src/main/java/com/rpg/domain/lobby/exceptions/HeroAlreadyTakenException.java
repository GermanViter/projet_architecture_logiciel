package com.rpg.domain.lobby.exceptions;

public final class HeroAlreadyTakenException extends LobbyException {
    public HeroAlreadyTakenException() {
        super("HERO_ALREADY_TAKEN", "Hero is already taken by another player.");
    }
}
