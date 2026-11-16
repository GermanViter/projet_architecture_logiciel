package com.rpg.infrastructure.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.anotherPseudonym;
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
        registry.addLobby(aLobbyHostedBy(OTHER_VALUE));

        assertThat(registry.allLobbies()).hasSize(2);
    }

    @Test
    void addLobby_playerWhoLeftTheirPreviousLobby_isStored() {
        Lobby previousLobby = aLobby();
        registry.addLobby(previousLobby);
        previousLobby.join(anotherPseudonym());
        previousLobby.leave(aPseudonym());
        Lobby newLobby = aLobby();

        registry.addLobby(newLobby);

        assertThat(registry.allLobbies()).containsExactly(previousLobby, newLobby);
    }

    @Test
    void removeLobby_storedLobby_removesOnlyThatLobby() {
        Lobby removedLobby = aLobby();
        Lobby keptLobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(removedLobby);
        registry.addLobby(keptLobby);

        registry.removeLobby(removedLobby);

        assertThat(registry.allLobbies()).containsExactly(keptLobby);
    }

    @Test
    void findById_idRebuiltFromItsValue_returnsTheLobby() {
        Lobby lobby = aLobby();
        registry.addLobby(lobby);

        assertThat(registry.findById(LobbyId.of(lobby.id().getValue()))).hasValue(lobby);
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        registry.addLobby(aLobby());

        assertThat(registry.findById(LobbyId.generate())).isEmpty();
    }

    @Test
    void hasLobbyWithMember_playerWhoJoinedAStoredLobby_isTrue() {
        Lobby lobby = aLobby();
        registry.addLobby(lobby);
        lobby.join(anotherPseudonym());

        assertThat(registry.hasLobbyWithMember(anotherPseudonym())).isTrue();
    }

    @Test
    void hasLobbyWithMember_playerInNoLobby_isFalse() {
        registry.addLobby(aLobby());

        assertThat(registry.hasLobbyWithMember(anotherPseudonym())).isFalse();
    }
}
