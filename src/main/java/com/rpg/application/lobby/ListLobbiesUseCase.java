package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ListLobbiesUseCase {
    private final LobbyRepository lobbyRepository;

    public ListLobbiesUseCase(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public ListLobbiesOutput execute() {
        List<LobbySummary> summaries = lobbyRepository.findAll().stream()
                .filter(lobby -> lobby.status().equals(com.rpg.domain.lobby.LobbyStatus.OPEN)
                        && lobby.availableSeats() > 0)
                .map(lobby -> new LobbySummary(
                        lobby.id().toString(),
                        lobby.host().toString(),
                        lobby.members().size(),
                        4 // MAX_PLAYERS is 4
                ))
                .collect(Collectors.toList());

        return new ListLobbiesOutput(summaries);
    }
}
