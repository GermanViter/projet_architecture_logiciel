package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;

import java.util.List;

public record LobbyOutput(String id, String hostId, List<LobbyMemberOutput> players,
                          int availableSeats) {

    public static LobbyOutput from(Lobby lobby) {
        return new LobbyOutput(
                lobby.id().getValue(),
                lobby.host().getValue(),
                lobby.members().stream().map(member -> {
                    var hero = lobby.getSelectedHero(member);
                    if (hero.isPresent()) {
                        return LobbyMemberOutput.from(member, hero.get());
                    }
                    return LobbyMemberOutput.from(member);
                }).toList(),
                lobby.availableSeats());
    }
}
