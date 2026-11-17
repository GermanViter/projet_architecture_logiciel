package com.rpg.domain.lobby.exceptions;

public final class HeroAlreadyTakenException extends LobbyException {
    public HeroAlreadyTakenException() {
        super("HERO_ALREADY_TAKEN", "This hero has already been selected by another player.");
    }
}
