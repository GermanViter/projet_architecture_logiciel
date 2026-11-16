package com.rpg.domain.lobby.fixtures;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.player.Pseudonym;
import com.rpg.domain.player.fixtures.PseudonymFixture;

public final class LobbyFixture {
    private LobbyFixture() {}

    public static Lobby aLobby() {
        return Lobby.openedBy(PseudonymFixture.aPseudonym());
    }

    public static Lobby aLobbyHostedBy(String pseudonym) {
        return Lobby.openedBy(new Pseudonym(pseudonym));
    }
}
