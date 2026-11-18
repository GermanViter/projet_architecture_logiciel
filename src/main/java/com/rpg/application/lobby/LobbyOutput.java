package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;

import java.util.List;

public record LobbyOutput(String id, String hostId, List<LobbyMemberOutput> players,
                          int availableSeats, boolean started) {

    public static LobbyOutput from(Lobby lobby) {
        return new LobbyOutput(
                lobby.id().getValue(),
                lobby.host().getValue(),
                lobby.members().stream().map(member -> {
                    String hero = lobby.getSelectedHero(member)
                            .map(h -> h.name().getName())
                            .orElse(null);
                    return new LobbyMemberOutput(member.getValue(), member.getValue(), hero);
                }).toList(),
                lobby.availableSeats(),
                lobby.isStarted());
    }
}
