package com.rpg.domain.lobby;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroFactory;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.species.Species;
import com.rpg.domain.lobby.exceptions.GameAlreadyStartedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadySelectedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadyTakenException;
import com.rpg.domain.lobby.exceptions.LobbyFullException;
import com.rpg.domain.lobby.exceptions.NotHostException;
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
import java.util.List;
import java.util.Optional;

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

        assertThat(lobby.members()).containsExactly(aPseudonym());
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

        assertThat(lobby.members()).containsExactly(aPseudonym(), anotherPseudonym()).inOrder();
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

        assertThat(lobby.members()).containsExactly(aPseudonym());
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

        assertThat(lobby.members()).containsExactly(aPseudonym());
    }

    @Test
    void startGame_hostStartsGame_marksAsStarted() {
        Lobby lobby = aLobby();

        lobby.startGame(aPseudonym());

        assertThat(lobby.isStarted()).isTrue();
    }

    @Test
    void startGame_gameAlreadyStarted_isRefused() {
        Lobby lobby = aLobby();
        lobby.startGame(aPseudonym());

        assertThrows(GameAlreadyStartedException.class, () -> lobby.startGame(aPseudonym()));
    }

    @Test
    void startGame_nonHost_isRefused() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());

        assertThrows(NotHostException.class, () -> lobby.startGame(anotherPseudonym()));
    }

    @Test
    void join_afterGameStarted_isRefused() {
        Lobby lobby = aLobby();
        lobby.startGame(aPseudonym());

        assertThrows(GameAlreadyStartedException.class, () -> lobby.join(anotherPseudonym()));
    }

    @Test
    void leave_afterGameStarted_isRefused() {
        Lobby lobby = aLobby();
        lobby.startGame(aPseudonym());

        assertThrows(GameAlreadyStartedException.class, () -> lobby.leave(aPseudonym()));
    }

    private static final Hero HERO = new HeroFactory().create(
            new HeroName("Gandalf"), Species.HUMAN, new Warrior(),
            List.of(new Characteristic(Characteristics.STRENGTH, 10),
                    new Characteristic(Characteristics.CONSTITUTION, 10)));

    @Test
    void selectHero_playerSelectsHero_hasSelectedHero() {
        Lobby lobby = aLobby();

        lobby.selectHero(aPseudonym(), HERO);

        assertThat(lobby.getSelectedHero(aPseudonym())).hasValue(HERO);
    }

    @Test
    void selectHero_heroAlreadyTaken_isRefused() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.selectHero(aPseudonym(), HERO);

        assertThrows(HeroAlreadyTakenException.class, () -> lobby.selectHero(anotherPseudonym(), HERO));
    }

    @Test
    void selectHero_playerAlreadySelected_isRefused() {
        Lobby lobby = aLobby();
        lobby.selectHero(aPseudonym(), HERO);

        Hero anotherHero = new HeroFactory().create(
                new HeroName("Aragorn"), Species.HUMAN, new Warrior(),
                List.of(new Characteristic(Characteristics.STRENGTH, 10),
                        new Characteristic(Characteristics.CONSTITUTION, 10)));
        assertThrows(HeroAlreadySelectedException.class, () -> lobby.selectHero(aPseudonym(), anotherHero));
    }

    @Test
    void leave_playerLeaves_clearsHeroSelection() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        lobby.selectHero(anotherPseudonym(), HERO);

        lobby.leave(anotherPseudonym());

        assertThat(lobby.getSelectedHero(anotherPseudonym())).isEmpty();
    }
}
