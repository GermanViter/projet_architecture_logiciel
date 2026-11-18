package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;

import java.util.List;

public record LobbyOutput(String id, String hostId, List<LobbyMemberOutput> players,
                          int availableSeats) {

    public static LobbyOutput from(Lobby lobby) {
        return new LobbyOutput(
                lobby.id().getValue(),
                lobby.host().getValue(),
                lobby.members().stream().map(LobbyMemberOutput::from).toList(),
                lobby.availableSeats());
    }
}
