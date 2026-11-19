package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import org.junit.jupiter.api.Test;

import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyOutputTest {

    @Test
    void from_lobbyLeftByItsLastMember_isRefusedAsNotFound() {
        Lobby lobby = aLobby();
        lobby.leave(aPseudonym());

        assertThrows(LobbyNotFoundException.class, () -> LobbyOutput.from(lobby));
    }
}
