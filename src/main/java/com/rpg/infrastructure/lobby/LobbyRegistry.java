package com.rpg.infrastructure.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;

import java.util.ArrayList;
import java.util.List;

public final class LobbyRegistry implements LobbyRepository {
    private final List<Lobby> lobbies = new ArrayList<>();

    @Override
    public synchronized void addLobby(Lobby lobby) {
        if (hostIsAlreadyInALobby(lobby)) {
            throw new PlayerAlreadyInLobbyException();
        }
        lobbies.add(lobby);
    }

    public synchronized List<Lobby> allLobbies() {
        return new ArrayList<>(lobbies);
    }

    private boolean hostIsAlreadyInALobby(Lobby lobby) {
        return lobbies.stream().anyMatch(existing -> existing.hasMember(lobby.host()));
    }
}
