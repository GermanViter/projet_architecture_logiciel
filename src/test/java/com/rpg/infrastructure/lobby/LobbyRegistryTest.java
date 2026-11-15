package com.rpg.infrastructure.lobby;

import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyRegistryTest {

    private final LobbyRegistry registry = new LobbyRegistry();

    @Test
    void addLobby_validLobby_isStored() {
        registry.addLobby(aLobby());

        assertThat(registry.allLobbies()).hasSize(1);
    }

    @Test
    void addLobby_hostAlreadyInALobby_isRefused() {
        registry.addLobby(aLobby());

        assertThrows(PlayerAlreadyInLobbyException.class, () -> registry.addLobby(aLobby()));
    }

    @Test
    void addLobby_hostAlreadyInALobby_doesNotStoreTheSecondOne() {
        registry.addLobby(aLobby());

        assertThrows(PlayerAlreadyInLobbyException.class, () -> registry.addLobby(aLobby()));

        assertThat(registry.allLobbies()).hasSize(1);
    }

    @Test
    void addLobby_differentHosts_areBothStored() {
        registry.addLobby(aLobby());
        registry.addLobby(aLobbyHostedBy("Bob"));

        assertThat(registry.allLobbies()).hasSize(2);
    }
}
