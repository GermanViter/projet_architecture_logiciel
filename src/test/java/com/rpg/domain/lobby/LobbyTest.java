package com.rpg.domain.lobby;

import com.rpg.domain.hero.HeroName;
import com.rpg.domain.lobby.exceptions.HeroAlreadyTakenException;
import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.LobbyNotOpenException;
import com.rpg.domain.lobby.exceptions.NotHostException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
import com.rpg.domain.lobby.exceptions.PlayersNotReadyException;
import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.hero.fixtures.HeroFixture.HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.OTHER_HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.aHero;
import static com.rpg.domain.hero.fixtures.HeroFixture.anotherHero;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MAX_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MIN_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyReadyToStartWithMembers;
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
    void snapshot_lobbyWithTwoMembers_holdsTheHostStatusAndMembersOfTheSameState() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());

        LobbySnapshot snapshot = lobby.snapshot();

        assertThat(snapshot).isEqualTo(new LobbySnapshot(
                Optional.of(aPseudonym()),
                LobbyStatus.OPEN,
                List.of(new LobbyMember(aPseudonym()), new LobbyMember(anotherPseudonym()))));
    }

    @Test
    void snapshot_lobbyLeftByItsLastMember_hasNoHostAndNoMembers() {
        Lobby lobby = aLobby();
        lobby.leave(aPseudonym());

        LobbySnapshot snapshot = lobby.snapshot();

        assertThat(snapshot.host()).isEmpty();
        assertThat(snapshot.members()).isEmpty();
    }

    @Test
    void leave_nonMember_isRefusedAndMembersStayTheSame() {
        Lobby lobby = aLobby();

        assertThrows(PlayerNotInLobbyException.class, () -> lobby.leave(anotherPseudonym()));

        assertThat(lobby.members()).containsExactly(new LobbyMember(aPseudonym()));
    }

    @Test
    void selectHero_member_hasThatHeroSelected() {
        Lobby lobby = aLobby();

        lobby.selectHero(aPseudonym(), aHero());

        assertThat(selectedHeroOf(lobby, aPseudonym())).hasValue(new HeroName(HERO_NAME));
    }

    @Test
    void selectHero_nonMember_isRefused() {
        Lobby lobby = aLobby();

        assertThrows(PlayerNotInLobbyException.class, () -> lobby.selectHero(anotherPseudonym(), aHero()));
    }

    @Test
    void selectHero_heroTakenByAnotherMember_isRefusedAndNothingIsSelected() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.selectHero(anotherPseudonym(), aHero());

        assertThrows(HeroAlreadyTakenException.class, () -> lobby.selectHero(aPseudonym(), aHero()));

        assertThat(selectedHeroOf(lobby, aPseudonym())).isEmpty();
    }

    @Test
    void selectHero_sameHeroAgain_keepsItSelected() {
        Lobby lobby = aLobby();
        lobby.selectHero(aPseudonym(), aHero());

        lobby.selectHero(aPseudonym(), aHero());

        assertThat(selectedHeroOf(lobby, aPseudonym())).hasValue(new HeroName(HERO_NAME));
    }

    @Test
    void selectHero_anotherHero_replacesTheSelection() {
        Lobby lobby = aLobby();
        lobby.selectHero(aPseudonym(), aHero());

        lobby.selectHero(aPseudonym(), anotherHero());

        assertThat(selectedHeroOf(lobby, aPseudonym())).hasValue(new HeroName(OTHER_HERO_NAME));
    }

    @Test
    void selectHero_heroReplacedByItsOwner_canBeSelectedByAnotherMember() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.selectHero(aPseudonym(), aHero());
        lobby.selectHero(aPseudonym(), anotherHero());

        lobby.selectHero(anotherPseudonym(), aHero());

        assertThat(selectedHeroOf(lobby, anotherPseudonym())).hasValue(new HeroName(HERO_NAME));
    }

    @Test
    void leave_memberWithAHero_freesTheHeroForTheOthers() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.selectHero(anotherPseudonym(), aHero());
        lobby.leave(anotherPseudonym());

        lobby.selectHero(aPseudonym(), aHero());

        assertThat(selectedHeroOf(lobby, aPseudonym())).hasValue(new HeroName(HERO_NAME));
    }

    @Test
    void startGame_hostAloneWithAHero_startsTheGame() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MIN_PLAYERS);

        lobby.startGame(aPseudonym());

        assertThat(lobby.status()).isEqualTo(LobbyStatus.STARTED);
    }

    @Test
    void startGame_fullLobbyWhereEveryoneHasAHero_startsTheGame() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MAX_PLAYERS);

        lobby.startGame(aPseudonym());

        assertThat(lobby.status()).isEqualTo(LobbyStatus.STARTED);
    }

    @Test
    void startGame_oneMemberWithoutAHero_isRefusedAndLobbyStaysOpen() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MAX_PLAYERS - 1);
        lobby.join(anotherPseudonym());

        assertThrows(PlayersNotReadyException.class, () -> lobby.startGame(aPseudonym()));

        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void startGame_byAMemberWhoIsNotTheHost_isRefusedAndLobbyStaysOpen() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MIN_PLAYERS);
        lobby.join(anotherPseudonym());
        lobby.selectHero(anotherPseudonym(), anotherHero());

        assertThrows(NotHostException.class, () -> lobby.startGame(anotherPseudonym()));

        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void startGame_afterTheLastMemberLeft_isRefusedAndLobbyStaysOpen() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MIN_PLAYERS);
        lobby.leave(aPseudonym());

        assertThrows(NotHostException.class, () -> lobby.startGame(aPseudonym()));

        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void startGame_alreadyStarted_isRefused() {
        Lobby lobby = aStartedLobby();

        assertThrows(LobbyNotOpenException.class, () -> lobby.startGame(aPseudonym()));
    }

    @Test
    void join_afterGameStarted_isRefused() {
        Lobby lobby = aStartedLobby();

        assertThrows(LobbyNotOpenException.class, () -> lobby.join(anotherPseudonym()));
    }

    @Test
    void leave_afterGameStarted_isRefusedAndGroupStaysTheSame() {
        Lobby lobby = aStartedLobby();

        assertThrows(LobbyNotOpenException.class, () -> lobby.leave(aPseudonym()));

        assertThat(lobby.hasMember(aPseudonym())).isTrue();
    }

    @Test
    void selectHero_afterGameStarted_isRefusedAndHeroStaysTheSame() {
        Lobby lobby = aStartedLobby();

        assertThrows(LobbyNotOpenException.class, () -> lobby.selectHero(aPseudonym(), anotherHero()));

        assertThat(selectedHeroOf(lobby, aPseudonym())).hasValue(new HeroName(HERO_NAME));
    }

    @Test
    void isJoinable_startedLobbyWithFreeSeats_isFalse() {
        Lobby lobby = aStartedLobby();

        assertThat(lobby.isJoinable()).isFalse();
    }

    private static Lobby aStartedLobby() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MIN_PLAYERS);
        lobby.startGame(aPseudonym());
        return lobby;
    }

    private static Optional<HeroName> selectedHeroOf(Lobby lobby, Pseudonym player) {
        return lobby.members().stream()
                .filter(member -> member.pseudonym().equals(player))
                .findFirst()
                .flatMap(LobbyMember::selectedHeroName);
    }
}
