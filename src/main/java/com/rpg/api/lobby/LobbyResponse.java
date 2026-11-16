package com.rpg.api.lobby;

import com.rpg.application.lobby.LobbyOutput;

import java.util.List;

public record LobbyResponse(String id, String hostId, List<LobbyMemberResponse> players,
                            int availableSeats) {

    public static LobbyResponse from(LobbyOutput lobby) {
        return new LobbyResponse(
                lobby.id(),
                lobby.hostId(),
                lobby.players().stream().map(LobbyMemberResponse::from).toList(),
                lobby.availableSeats());
    }
}
