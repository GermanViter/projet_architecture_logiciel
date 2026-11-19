package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.session.exceptions.InvalidSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MAX_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.UNKNOWN_LOBBY_ID;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinLobbyUseCaseTest {
    private static final int MEMBERS_AFTER_JOIN = 2;
    private static final String NO_HERO_SELECTED = null;

    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    @Mock
    private LobbyRepository lobbies;

    private final Lobby lobbyHostedByAnotherPlayer = aLobbyHostedBy(OTHER_VALUE);

    private JoinLobbyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new JoinLobbyUseCase(new ConnectedPlayerFinder(sessions, players), lobbies);
    }

    @Test
    void execute_connectedPlayer_asksTheRegistryToJoinTheLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);

        useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer));

        verify(lobbies).joinLobby(lobbyHostedByAnotherPlayer, aPseudonym());
    }

    @Test
    void execute_connectedPlayer_returnsTheNewMemberAndTheUpdatedLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);
        whenTheRegistryAcceptsTheJoin(lobbyHostedByAnotherPlayer);

        JoinLobbyOutput output = useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer));

        assertThat(output).isEqualTo(new JoinLobbyOutput(VALID_VALUE, new LobbyOutput(
                lobbyHostedByAnotherPlayer.id().getValue(),
                OTHER_VALUE,
                LobbyStatus.OPEN.name(),
                List.of(new LobbyMemberOutput(OTHER_VALUE, OTHER_VALUE, NO_HERO_SELECTED),
                        new LobbyMemberOutput(VALID_VALUE, VALID_VALUE, NO_HERO_SELECTED)),
                MAX_PLAYERS - MEMBERS_AFTER_JOIN)));
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class,
                () -> useCase.execute(new JoinLobbyCommand(VALID_TOKEN, UNKNOWN_LOBBY_ID)));
    }

    @Test
    void execute_joinRefusedByTheRegistry_isRefused() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);
        doThrow(new PlayerAlreadyInLobbyException())
                .when(lobbies).joinLobby(lobbyHostedByAnotherPlayer, aPseudonym());

        assertThrows(PlayerAlreadyInLobbyException.class,
                () -> useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer)));
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer)));

        verifyNoInteractions(lobbies);
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }

    private void whenLobbyIsStored(Lobby lobby) {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private void whenTheRegistryAcceptsTheJoin(Lobby lobby) {
        doAnswer(invocation -> {
            lobby.join(aPseudonym());
            return null;
        }).when(lobbies).joinLobby(lobby, aPseudonym());
    }

    private static JoinLobbyCommand joinCommandFor(Lobby lobby) {
        return new JoinLobbyCommand(VALID_TOKEN, lobby.id().getValue());
    }
}
