package com.rpg.domain.lobby;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.lobby.exceptions.GameAlreadyStartedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadySelectedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadyTakenException;
import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Lobby {
    private static final int MAX_PLAYERS = 4;

    private final LobbyId id;
    private final List<Pseudonym> members = new ArrayList<>();
    private final Map<Pseudonym, Hero> selectedHeroes = new HashMap<>();
    private boolean started = false;

    private Lobby(LobbyId id, Pseudonym host) {
        this.id = id;
        this.members.add(host);
    }

    public static Lobby openedBy(Pseudonym host) {
        return new Lobby(LobbyId.generate(), host);
    }

    public LobbyId id() {
        return id;
    }

    public synchronized Pseudonym host() {
        return members.getFirst();
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

    public synchronized void leave(Pseudonym player) {
        if (!members.remove(player)) {
            throw new PlayerNotInLobbyException();
        }
        clearHeroSelection(player);
    }

    public synchronized boolean hasMember(Pseudonym pseudonym) {
        return members.contains(pseudonym);
    }

    public synchronized boolean isEmpty() {
        return members.isEmpty();
    }

    public synchronized List<Pseudonym> members() {
        return List.copyOf(members);
    }

    public synchronized int availableSeats() {
        return MAX_PLAYERS - members.size();
    }

    public synchronized void selectHero(Pseudonym player, Hero hero) {
        if (started) {
            throw new GameAlreadyStartedException();
        }
        if (!members.contains(player)) {
            throw new PlayerNotInLobbyException();
        }
        if (selectedHeroes.containsKey(player)) {
            throw new HeroAlreadySelectedException();
        }
        if (isHeroTaken(hero)) {
            throw new HeroAlreadyTakenException();
        }
        selectedHeroes.put(player, hero);
    }

    public synchronized Optional<Hero> getSelectedHero(Pseudonym player) {
        return Optional.ofNullable(selectedHeroes.get(player));
    }

    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized void startGame() {
        started = true;
    }

    private boolean isHeroTaken(Hero hero) {
        return selectedHeroes.containsValue(hero);
    }

    public synchronized void clearHeroSelection(Pseudonym player) {
        selectedHeroes.remove(player);
    }
}
