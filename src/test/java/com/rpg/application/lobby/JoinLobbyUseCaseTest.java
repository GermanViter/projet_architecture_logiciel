package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import com.rpg.domain.lobby.exceptions.PlayerAlreadyMemberException;
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
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinLobbyUseCaseTest {
    private static final String TOKEN = "a-valid-token";
    private static final String UNKNOWN_LOBBY_ID = "an-unknown-lobby";
    private static final int MEMBERS_AFTER_JOIN = 2;

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
    void execute_connectedPlayer_becomesAMemberOfTheLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);

        useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer));

        assertThat(lobbyHostedByAnotherPlayer.hasMember(aPseudonym())).isTrue();
    }

    @Test
    void execute_connectedPlayer_returnsTheNewMemberAndTheUpdatedLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);

        JoinLobbyOutput output = useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer));

        assertThat(output).isEqualTo(new JoinLobbyOutput(VALID_VALUE, new LobbyOutput(
                lobbyHostedByAnotherPlayer.id().getValue(),
                OTHER_VALUE,
                List.of(new LobbyMemberOutput(OTHER_VALUE, OTHER_VALUE),
                        new LobbyMemberOutput(VALID_VALUE, VALID_VALUE)),
                MAX_PLAYERS - MEMBERS_AFTER_JOIN)));
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class,
                () -> useCase.execute(new JoinLobbyCommand(TOKEN, UNKNOWN_LOBBY_ID)));
    }

    @Test
    void execute_playerAlreadyInAnotherOpenLobby_isRefusedAndDoesNotJoin() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyHostedByAnotherPlayer);
        when(lobbies.hasLobbyWithMember(aPseudonym())).thenReturn(true);

        assertThrows(PlayerAlreadyInLobbyException.class,
                () -> useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer)));

        assertThat(lobbyHostedByAnotherPlayer.hasMember(aPseudonym())).isFalse();
    }

    @Test
    void execute_playerAlreadyMemberOfThisLobby_isRefusedAsAlreadyMember() {
        Lobby lobbyOfThePlayer = aLobby();
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyOfThePlayer);
        when(lobbies.hasLobbyWithMember(aPseudonym())).thenReturn(true);

        assertThrows(PlayerAlreadyMemberException.class,
                () -> useCase.execute(joinCommandFor(lobbyOfThePlayer)));
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(joinCommandFor(lobbyHostedByAnotherPlayer)));

        verifyNoInteractions(lobbies);
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }

    private void whenLobbyIsStored(Lobby lobby) {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private static JoinLobbyCommand joinCommandFor(Lobby lobby) {
        return new JoinLobbyCommand(TOKEN, lobby.id().getValue());
    }
}
