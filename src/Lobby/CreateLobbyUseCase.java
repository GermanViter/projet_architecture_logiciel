package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.Pseudonym;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CreateLobbyUseCase {
    private final PlayerRepository playerRepository;
    private final Map<UUID, Lobby> lobbies = new ConcurrentHashMap<>();

    public CreateLobbyUseCase(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Lobby execute(String pseudonymValue) {
        Pseudonym pseudonym = new Pseudonym(pseudonymValue);

        if (!playerRepository.exists(pseudonym)) {
            throw new PlayerNotFoundException(pseudonymValue);
        }

        Lobby lobby = new Lobby(pseudonym);
        lobbies.put(lobby.id(), lobby);

        return lobby;
    }

    public static final class PlayerNotFoundException extends RuntimeException {
        public PlayerNotFoundException(String pseudonym) {
            super("Player not found: " + pseudonym);
        }
    }
}