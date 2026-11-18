package com.rpg.domain.lobby;

import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MAX_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyWithMembers;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aThirdPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.anotherPseudonym;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyTest {

    @Test
    void openedBy_player_makesThatPlayerTheHost() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.host()).isEqualTo(aPseudonym());
    }

    @Test
    void openedBy_player_makesTheHostTheFirstMember() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.members()).containsExactly(new LobbyMember(aPseudonym()));
    }

    @Test
    void openedBy_player_leavesEverySeatButTheHostsAvailable() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.availableSeats()).isEqualTo(MAX_PLAYERS - 1);
    }

    @Test
    void hasMember_host_isTrue() {
        assertThat(aLobby().hasMember(aPseudonym())).isTrue();
    }

    @Test
    void hasMember_anotherPlayer_isFalse() {
        assertThat(aLobby().hasMember(anotherPseudonym())).isFalse();
    }

    @Test
    void openedBy_twice_givesDistinctIds() {
        assertThat(aLobby().id()).isNotEqualTo(aLobby().id());
    }

    @Test
    void join_newPlayer_becomesAMember() {
        Lobby lobby = aLobby();

        lobby.join(anotherPseudonym());

        assertThat(lobby.members()).containsExactly(new LobbyMember(aPseudonym()), new LobbyMember(anotherPseudonym())).inOrder();
    }

    @Test
    void join_newPlayer_takesOneAvailableSeat() {
        Lobby lobby = aLobby();
        int seatsBefore = lobby.availableSeats();

        lobby.join(anotherPseudonym());

        assertThat(lobby.availableSeats()).isEqualTo(seatsBefore - 1);
    }

    @Test
    void join_lastAvailableSeat_isAccepted() {
        Lobby lobby = aLobbyWithMembers(MAX_PLAYERS - 1);

        lobby.join(anotherPseudonym());

        assertThat(lobby.members()).hasSize(MAX_PLAYERS);
    }

    @Test
    void join_fullLobby_isRefusedAndStaysAtMaxPlayers() {
        Lobby lobby = aLobbyWithMembers(MAX_PLAYERS);

        assertThrows(LobbyFullException.class, () -> lobby.join(anotherPseudonym()));

        assertThat(lobby.members()).hasSize(MAX_PLAYERS);
    }

    @Test
    void openedBy_player_startsOpen() {
        Lobby lobby = Lobby.openedBy(aPseudonym());

        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void isJoinable_lobbyWithOneSeatLeft_isTrue() {
        Lobby lobby = aLobbyWithMembers(MAX_PLAYERS - 1);

        assertThat(lobby.isJoinable()).isTrue();
    }

    @Test
    void isJoinable_fullLobby_isFalse() {
        Lobby lobby = aLobbyWithMembers(MAX_PLAYERS);

        assertThat(lobby.isJoinable()).isFalse();
    }

    @Test
    void join_playerAlreadyMember_isRefused() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());

        assertThrows(PlayerAlreadyMemberException.class, () -> lobby.join(anotherPseudonym()));
    }

    @Test
    void leave_member_isNoLongerAMember() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());

        lobby.leave(anotherPseudonym());

        assertThat(lobby.members()).containsExactly(new LobbyMember(aPseudonym()));
    }

    @Test
    void leave_member_freesOneSeat() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        int seatsBefore = lobby.availableSeats();

        lobby.leave(anotherPseudonym());

        assertThat(lobby.availableSeats()).isEqualTo(seatsBefore + 1);
    }

    @Test
    void leave_hostOfThreePlayers_makesTheSecondArrivedTheHost() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.join(aThirdPseudonym());

        lobby.leave(aPseudonym());

        assertThat(lobby.host()).isEqualTo(anotherPseudonym());
    }

    @Test
    void leave_lastMember_leavesTheLobbyEmpty() {
        Lobby lobby = aLobby();

        lobby.leave(aPseudonym());

        assertThat(lobby.isEmpty()).isTrue();
    }

    @Test
    void leave_nonMember_isRefusedAndMembersStayTheSame() {
        Lobby lobby = aLobby();

        assertThrows(PlayerNotInLobbyException.class, () -> lobby.leave(anotherPseudonym()));

        assertThat(lobby.members()).containsExactly(new LobbyMember(aPseudonym()));
    }
}
