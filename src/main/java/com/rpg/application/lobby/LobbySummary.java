package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;

public record LobbySummary(
        String id,
        String hostId,
        int playerCount,
        int maxPlayers
) {
    public static LobbySummary from(Lobby lobby) {
        return new LobbySummary(
                lobby.id().getValue(),
                lobby.host().getValue(),
                lobby.members().size(),
                lobby.capacity()
        );
    }
}
