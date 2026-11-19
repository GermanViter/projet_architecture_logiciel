package com.rpg.infrastructure.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import com.rpg.domain.player.Pseudonym;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LobbyRegistry implements LobbyRepository {
    private final List<Lobby> lobbies = new ArrayList<>();

    @Override
    public synchronized void addLobby(Lobby lobby) {
        if (hasLobbyWithMember(lobby.host())) {
            throw new PlayerAlreadyInLobbyException();
        }
        lobbies.add(lobby);
    }

    @Override
    public synchronized void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    @Override
    public synchronized void joinLobby(Lobby lobby, Pseudonym newcomer) {
        ensureNotInAnotherLobby(lobby, newcomer);
        lobby.join(newcomer);
    }

    @Override
    public synchronized Optional<Lobby> findById(LobbyId id) {
        return lobbies.stream().filter(lobby -> lobby.id().equals(id)).findFirst();
    }

    @Override
    public synchronized boolean hasLobbyWithMember(Pseudonym player) {
        return lobbies.stream().anyMatch(existing -> existing.hasMember(player));
    }

    @Override
    public synchronized List<Lobby> findAll() {
        return new ArrayList<>(lobbies);
    }

    private void ensureNotInAnotherLobby(Lobby lobby, Pseudonym newcomer) {
        if (hasLobbyWithMember(newcomer) && !lobby.hasMember(newcomer)) {
            throw new PlayerAlreadyInLobbyException();
        }
    }
}
