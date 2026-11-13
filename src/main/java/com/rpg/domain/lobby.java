package com.rpg.domain;

import com.rpg.domain.player.Pseudonym;

import java.util.UUID;

public final class Lobby {
    private final UUID id;
    private final Pseudonym host;

    public Lobby(Pseudonym host) {
        this.id = UUID.randomUUID();
        this.host = host;
    }

    public UUID id() {
        return id;
    }

    public Pseudonym host() {
        return host;
    }
}