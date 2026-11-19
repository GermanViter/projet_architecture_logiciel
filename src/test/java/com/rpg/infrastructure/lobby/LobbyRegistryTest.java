package com.rpg.infrastructure.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.exceptions.LobbyNotOpenException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.THIRD_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.anotherPseudonym;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyRegistryTest {
    private static final long ONE_SUCCESS = 1;
    private static final long ONE_REFUSAL = 1;

    private final LobbyRegistry registry = new LobbyRegistry();

    @Test
    void addLobby_validLobby_isStored() {
        registry.addLobby(aLobby());

        assertThat(registry.findAll()).hasSize(1);
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

        assertThat(registry.findAll()).hasSize(1);
    }

    @Test
    void addLobby_differentHosts_areBothStored() {
        registry.addLobby(aLobby());
        registry.addLobby(aLobbyHostedBy(OTHER_VALUE));

        assertThat(registry.findAll()).hasSize(2);
    }

    @Test
    void addLobby_playerWhoLeftTheirPreviousLobby_isStored() {
        Lobby previousLobby = aLobby();
        registry.addLobby(previousLobby);
        previousLobby.join(anotherPseudonym());
        previousLobby.leave(aPseudonym());
        Lobby newLobby = aLobby();

        registry.addLobby(newLobby);

        assertThat(registry.findAll()).containsExactly(previousLobby, newLobby);
    }

    @Test
    void removeLobby_storedLobby_removesOnlyThatLobby() {
        Lobby removedLobby = aLobby();
        Lobby keptLobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(removedLobby);
        registry.addLobby(keptLobby);

        registry.removeLobby(removedLobby);

        assertThat(registry.findAll()).containsExactly(keptLobby);
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

    @Test
    void joinLobby_playerInNoLobby_becomesAMember() {
        Lobby lobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(lobby);

        registry.joinLobby(lobby, aPseudonym());

        assertThat(lobby.hasMember(aPseudonym())).isTrue();
    }

    @Test
    void joinLobby_hostOfAnotherStoredLobby_isRefusedAndDoesNotJoin() {
        Lobby lobbyOfThePlayer = aLobby();
        Lobby otherLobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(lobbyOfThePlayer);
        registry.addLobby(otherLobby);

        assertThrows(PlayerAlreadyInLobbyException.class,
                () -> registry.joinLobby(otherLobby, aPseudonym()));

        assertThat(otherLobby.hasMember(aPseudonym())).isFalse();
    }

    @Test
    void joinLobby_memberOfThisLobby_isRefusedAsAlreadyMember() {
        Lobby lobbyOfThePlayer = aLobby();
        registry.addLobby(lobbyOfThePlayer);

        assertThrows(PlayerAlreadyMemberException.class,
                () -> registry.joinLobby(lobbyOfThePlayer, aPseudonym()));
    }

    @Test
    void joinLobby_lobbyRemovedAfterItsLastMemberLeft_isRefusedAndDoesNotJoin() {
        Lobby lobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(lobby);
        lobby.leave(anotherPseudonym());
        registry.removeLobby(lobby);

        assertThrows(LobbyNotOpenException.class, () -> registry.joinLobby(lobby, aPseudonym()));

        assertThat(lobby.hasMember(aPseudonym())).isFalse();
    }

    @Test
    void addLobby_hostWhoJoinedAnotherStoredLobby_isRefused() {
        Lobby joinedLobby = aLobbyHostedBy(OTHER_VALUE);
        registry.addLobby(joinedLobby);
        registry.joinLobby(joinedLobby, aPseudonym());

        assertThrows(PlayerAlreadyInLobbyException.class, () -> registry.addLobby(aLobby()));
    }

    @Test
    void joinLobby_samePlayerJoiningTwoLobbiesAtOnce_succeedsInExactlyOne() throws InterruptedException {
        Lobby firstLobby = aLobbyHostedBy(OTHER_VALUE);
        Lobby secondLobby = aLobbyHostedBy(THIRD_VALUE);
        registry.addLobby(firstLobby);
        registry.addLobby(secondLobby);

        List<Throwable> outcomes = joinConcurrently(aPseudonym(), firstLobby, secondLobby);

        assertThat(outcomes.stream().filter(Objects::isNull).count()).isEqualTo(ONE_SUCCESS);
        assertThat(outcomes.stream().filter(PlayerAlreadyInLobbyException.class::isInstance).count())
                .isEqualTo(ONE_REFUSAL);
        assertThat(firstLobby.hasMember(aPseudonym())).isNotEqualTo(secondLobby.hasMember(aPseudonym()));
    }

    private List<Throwable> joinConcurrently(Pseudonym player, Lobby... targets) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(targets.length);
        try {
            List<Future<Throwable>> attempts = new ArrayList<>();
            for (Lobby target : targets) {
                attempts.add(executor.submit(() -> {
                    startSignal.await();
                    return attemptToJoin(target, player);
                }));
            }
            startSignal.countDown();
            List<Throwable> outcomes = new ArrayList<>();
            for (Future<Throwable> attempt : attempts) {
                outcomes.add(attempt.get());
            }
            return outcomes;
        } catch (ExecutionException e) {
            throw new AssertionError(e.getCause());
        } finally {
            executor.shutdownNow();
        }
    }

    private Throwable attemptToJoin(Lobby target, Pseudonym player) {
        try {
            registry.joinLobby(target, player);
            return null;
        } catch (RuntimeException refusal) {
            return refusal;
        }
    }
}
