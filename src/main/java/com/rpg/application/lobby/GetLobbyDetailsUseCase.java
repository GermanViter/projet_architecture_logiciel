package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class GetLobbyDetailsUseCase {
    private final LobbyRepository lobbyRepository;

    public GetLobbyDetailsUseCase(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public GetLobbyDetailsOutput execute(String lobbyIdString) {
        Lobby lobby = lobbyRepository.findById(com.rpg.domain.lobby.LobbyId.of(lobbyIdString))
                .orElseThrow(LobbyNotFoundException::new);

        List<LobbyMemberDetail> memberDetails = lobby.members().stream()
                .map(m -> new LobbyMemberDetail(
                        m.pseudonym().toString(),
                        m.hero() != null ? m.hero().id().toString() : null
                ))
                .collect(Collectors.toList());

        return new GetLobbyDetailsOutput(
                lobby.host().toString(),
                lobby.status().toString(),
                memberDetails
        );
    }
}
