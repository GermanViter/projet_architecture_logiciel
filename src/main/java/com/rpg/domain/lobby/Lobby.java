package com.rpg.domain.lobby;

import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.List;

public final class Lobby {
    private static final int MAX_PLAYERS = 4;

    private final LobbyId id;
    private final Pseudonym host;
    private final List<Pseudonym> members = new ArrayList<>();

    private Lobby(LobbyId id, Pseudonym host) {
        this.id = id;
        this.host = host;
        this.members.add(host);
    }

    public static Lobby openedBy(Pseudonym host) {
        return new Lobby(LobbyId.generate(), host);
    }

    public LobbyId id() {
        return id;
    }

    public Pseudonym host() {
        return host;
    }

    public synchronized void join(Pseudonym player) {
        if (members.contains(player)) {
            throw new PlayerAlreadyMemberException();
        }
        if (availableSeats() == 0) {
            throw new LobbyFullException();
        }
        members.add(player);
    }

    public synchronized boolean hasMember(Pseudonym pseudonym) {
        return members.contains(pseudonym);
    }

    public synchronized List<Pseudonym> members() {
        return List.copyOf(members);
    }

    public synchronized int availableSeats() {
        return MAX_PLAYERS - members.size();
    }
}
