package com.rpg.domain.lobby;

import com.rpg.domain.player.Pseudonym;

import java.util.List;
import java.util.Optional;

public interface LobbyRepository {
    void addLobby(Lobby lobby);
    void removeLobby(Lobby lobby);
    Optional<Lobby> findById(LobbyId id);
    boolean hasLobbyWithMember(Pseudonym player);
    List<Lobby> findAll();
}
