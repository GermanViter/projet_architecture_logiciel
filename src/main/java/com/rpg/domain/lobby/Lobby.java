package com.rpg.domain.lobby;

import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.List;

package com.rpg.domain.lobby;

import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Lobby {
    private static final int MAX_PLAYERS = 4;

    private final LobbyId id;
    private final List<LobbyMember> members = new ArrayList<>();
    private LobbyStatus status;

    private Lobby(LobbyId id, Pseudonym host) {
        this.id = id;
        this.status = LobbyStatus.OPEN;
        this.members.add(new LobbyMember(host));
    }

    public static Lobby openedBy(Pseudonym host) {
        return new Lobby(LobbyId.generate(), host);
    }

    public LobbyId id() {
        return id;
    }

    public synchronized LobbyStatus status() {
        return status;
    }

    public synchronized void setStatus(LobbyStatus status) {
        this.status = status;
    }

    public synchronized Pseudonym host() {
        return members.get(0).pseudonym();
    }

    public synchronized void join(Pseudonym player) {
        if (hasMember(player)) {
            throw new PlayerAlreadyMemberException();
        }
        if (availableSeats() == 0) {
            throw new LobbyFullException();
        }
        members.add(new LobbyMember(player));
    }

    public synchronized void leave(Pseudonym player) {
        LobbyMember member = findMember(player)
                .orElseThrow(() -> new PlayerNotInLobbyException());
        members.remove(member);
    }

    public synchronized boolean hasMember(Pseudonym pseudonym) {
        return findMember(pseudonym).isPresent();
    }

    public synchronized boolean isEmpty() {
        return members.isEmpty();
    }

    public synchronized List<LobbyMember> members() {
        return List.copyOf(members);
    }

    public synchronized int availableSeats() {
        return MAX_PLAYERS - members.size();
    }

    public synchronized void setReady(Pseudonym pseudonym, boolean ready) {
        findMember(pseudonym).ifPresent(m -> m.setReady(ready));
    }

    public synchronized void setHero(Pseudonym pseudonym, com.rpg.domain.hero.Hero hero) {
        findMember(pseudonym).ifPresent(m -> m.setHero(hero));
    }

    private Optional<LobbyMember> findMember(Pseudonym pseudonym) {
        return members.stream()
                .filter(m -> m.pseudonym().equals(pseudonym))
                .findFirst();
    }
}
