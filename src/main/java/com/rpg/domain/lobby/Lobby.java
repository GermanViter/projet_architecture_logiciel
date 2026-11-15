package com.rpg.domain.lobby;

import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.List;

public final class Lobby {
    private final LobbyId id;
    private final List<Pseudonym> members = new ArrayList<>();

    private Lobby(LobbyId id, Pseudonym host) {
        this.id = id;
        this.members.add(host);
    }

    /** The creator becomes the host and the first member of the lobby. */
    public static Lobby openedBy(Pseudonym host) {
        return new Lobby(LobbyId.generate(), host);
    }

    public LobbyId id() {
        return id;
    }

    public Pseudonym host() {
        return members.get(0);
    }

    public boolean hasMember(Pseudonym pseudonym) {
        return members.contains(pseudonym);
    }

    public List<Pseudonym> members() {
        return List.copyOf(members);
    }
}
