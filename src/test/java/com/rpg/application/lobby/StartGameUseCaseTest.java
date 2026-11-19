package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.lobby.exceptions.NotHostException;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.session.exceptions.InvalidSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MIN_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.UNKNOWN_LOBBY_ID;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyReadyToStartWithMembers;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartGameUseCaseTest {
    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    @Mock
    private LobbyRepository lobbies;

    private StartGameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new StartGameUseCase(new ConnectedPlayerFinder(sessions, players), lobbies);
    }

    @Test
    void execute_hostOfAReadyLobby_returnsTheStartedLobby() {
        Lobby lobbyHostedByThePlayer = aLobbyReadyToStartWithMembers(MIN_PLAYERS);
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByThePlayer);

        LobbyOutput output = useCase.execute(startCommandFor(lobbyHostedByThePlayer));

        assertThat(output.status()).isEqualTo(LobbyStatus.STARTED.name());
    }

    @Test
    void execute_memberWhoIsNotTheHost_isForbiddenAndLobbyStaysOpen() {
        Lobby lobbyHostedByAnotherPlayer = aLobbyHostedBy(OTHER_VALUE);
        lobbyHostedByAnotherPlayer.join(aPseudonym());
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);

        assertThrows(NotHostException.class,
                () -> useCase.execute(startCommandFor(lobbyHostedByAnotherPlayer)));

        assertThat(lobbyHostedByAnotherPlayer.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class,
                () -> useCase.execute(new StartGameCommand(VALID_TOKEN, UNKNOWN_LOBBY_ID)));
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        Lobby lobby = aLobbyReadyToStartWithMembers(MIN_PLAYERS);
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class, () -> useCase.execute(startCommandFor(lobby)));

        verifyNoInteractions(lobbies);
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }

    private void whenLobbyIsStored(Lobby lobby) {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private static StartGameCommand startCommandFor(Lobby lobby) {
        return new StartGameCommand(VALID_TOKEN, lobby.id().getValue());
    }
}
