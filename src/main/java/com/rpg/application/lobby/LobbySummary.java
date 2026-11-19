package com.rpg.application.lobby;

import com.rpg.domain.lobby.LobbySnapshot;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;

public record LobbySummary(
        String id,
        String hostId,
        int playerCount,
        int maxPlayers
) {
    public static LobbySummary from(LobbySnapshot lobby) {
        return new LobbySummary(
                lobby.id().getValue(),
                lobby.host().orElseThrow(LobbyNotFoundException::new).getValue(),
                lobby.members().size(),
                lobby.capacity()
        );
    }
}
