package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbySnapshot;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;

import java.util.List;

public record LobbyOutput(String id, String hostId, String status, List<LobbyMemberOutput> players,
                          int availableSeats) {

    public static LobbyOutput from(Lobby lobby) {
        LobbySnapshot snapshot = lobby.snapshot();
        return new LobbyOutput(
                snapshot.id().getValue(),
                snapshot.host().orElseThrow(LobbyNotFoundException::new).getValue(),
                snapshot.status().name(),
                snapshot.members().stream().map(LobbyMemberOutput::from).toList(),
                snapshot.availableSeats());
    }
}
