package com.rpg.domain.lobby;

import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;

class LobbyTest {

    @Test
    void openedBy_player_makesThatPlayerTheHost() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.host()).isEqualTo(aPseudonym());
    }

    @Test
    void openedBy_player_makesTheHostTheFirstMember() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.members()).containsExactly(aPseudonym());
    }

    @Test
    void hasMember_host_isTrue() {
        assertThat(aLobby().hasMember(aPseudonym())).isTrue();
    }

    @Test
    void hasMember_anotherPlayer_isFalse() {
        assertThat(aLobby().hasMember(new Pseudonym("Bob"))).isFalse();
    }

    @Test
    void openedBy_twice_givesDistinctIds() {
        assertThat(aLobby().id()).isNotEqualTo(aLobby().id());
    }
}
