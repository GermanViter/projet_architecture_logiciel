package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbySnapshot;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;
import java.util.List;

public class GetLobbyDetailsUseCase {
    private final LobbyRepository lobbyRepository;

    public GetLobbyDetailsUseCase(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public GetLobbyDetailsOutput execute(String lobbyIdString) {
        LobbySnapshot lobby = lobbyRepository.findById(LobbyId.of(lobbyIdString))
                .map(Lobby::snapshot)
                .orElseThrow(LobbyNotFoundException::new);
        Pseudonym host = lobby.host().orElseThrow(LobbyNotFoundException::new);

        List<LobbyMemberDetail> memberDetails = lobby.members().stream()
                .map(LobbyMemberDetail::from)
                .toList();

        return new GetLobbyDetailsOutput(
                host.getValue(),
                lobby.status().toString(),
                memberDetails
        );
    }
}
