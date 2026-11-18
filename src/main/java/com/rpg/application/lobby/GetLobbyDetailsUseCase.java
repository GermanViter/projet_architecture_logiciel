package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import java.util.List;

public class GetLobbyDetailsUseCase {
    private final LobbyRepository lobbyRepository;

    public GetLobbyDetailsUseCase(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public GetLobbyDetailsOutput execute(String lobbyIdString) {
        Lobby lobby = lobbyRepository.findById(LobbyId.of(lobbyIdString))
                .orElseThrow(LobbyNotFoundException::new);

        List<LobbyMemberDetail> memberDetails = lobby.members().stream()
                .map(LobbyMemberDetail::from)
                .toList();

        return new GetLobbyDetailsOutput(
                lobby.host().getValue(),
                lobby.status().toString(),
                memberDetails
        );
    }
}
