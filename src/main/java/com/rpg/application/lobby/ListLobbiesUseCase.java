package com.rpg.application.lobby;

import com.rpg.domain.lobby.LobbyRepository;
import java.util.List;

public class ListLobbiesUseCase {
    private final LobbyRepository lobbyRepository;

    public ListLobbiesUseCase(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public ListLobbiesOutput execute() {
        List<LobbySummary> summaries = lobbyRepository.findAll().stream()
                .filter(com.rpg.domain.lobby.Lobby::isJoinable)
                .map(LobbySummary::from)
                .toList();

        return new ListLobbiesOutput(summaries);
    }
}