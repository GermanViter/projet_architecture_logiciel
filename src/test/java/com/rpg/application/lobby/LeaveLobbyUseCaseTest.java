package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotRemoveAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.lobby.exceptions.PlayerNotInLobbyException;
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
import static com.rpg.domain.lobby.fixtures.LobbyFixture.UNKNOWN_LOBBY_ID;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyHostedBy;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.MIN_LENGTH;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.anotherPseudonym;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveLobbyUseCaseTest {
    private static final String PLAYER_ID_TOO_SHORT_FOR_A_PSEUDONYM =
            VALID_VALUE.substring(0, MIN_LENGTH - 1);

    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    @Mock
    private LobbyRepository lobbies;

    private final Lobby lobbyJoinedByThePlayer = aLobbyJoinedByThePlayer();

    private LeaveLobbyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LeaveLobbyUseCase(new ConnectedPlayerFinder(sessions, players), lobbies);
    }

    @Test
    void execute_member_isNoLongerAMemberOfTheLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyJoinedByThePlayer);

        useCase.execute(leaveCommand(lobbyJoinedByThePlayer, VALID_VALUE));

        assertThat(lobbyJoinedByThePlayer.hasMember(aPseudonym())).isFalse();
    }

    @Test
    void execute_otherMembersRemain_keepsTheLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyJoinedByThePlayer);

        useCase.execute(leaveCommand(lobbyJoinedByThePlayer, VALID_VALUE));

        verify(lobbies, never()).removeLobby(any());
    }

    @Test
    void execute_lastMember_removesTheLobby() {
        Lobby lobbyWithOnlyThePlayer = aLobby();
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyWithOnlyThePlayer);

        useCase.execute(leaveCommand(lobbyWithOnlyThePlayer, VALID_VALUE));

        verify(lobbies).removeLobby(lobbyWithOnlyThePlayer);
    }

    @Test
    void execute_anotherPlayersId_isForbiddenAndNothingChanges() {
        whenTokenBelongsToAPlayer();

        assertThrows(CannotRemoveAnotherPlayerException.class,
                () -> useCase.execute(leaveCommand(lobbyJoinedByThePlayer, OTHER_VALUE)));

        assertThat(lobbyJoinedByThePlayer.hasMember(anotherPseudonym())).isTrue();
        verifyNoInteractions(lobbies);
    }

    @Test
    void execute_playerIdTooShortForAPseudonym_isForbidden() {
        whenTokenBelongsToAPlayer();

        assertThrows(CannotRemoveAnotherPlayerException.class,
                () -> useCase.execute(leaveCommand(lobbyJoinedByThePlayer,
                        PLAYER_ID_TOO_SHORT_FOR_A_PSEUDONYM)));
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class,
                () -> useCase.execute(new LeaveLobbyCommand(VALID_TOKEN, UNKNOWN_LOBBY_ID, VALID_VALUE)));
    }

    @Test
    void execute_playerNotInTheLobby_isRefused() {
        Lobby lobbyWithoutThePlayer = aLobbyHostedBy(OTHER_VALUE);
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyWithoutThePlayer);

        assertThrows(PlayerNotInLobbyException.class,
                () -> useCase.execute(leaveCommand(lobbyWithoutThePlayer, VALID_VALUE)));
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(leaveCommand(lobbyJoinedByThePlayer, VALID_VALUE)));

        verifyNoInteractions(lobbies);
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }

    private void whenLobbyIsStored(Lobby lobby) {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private static Lobby aLobbyJoinedByThePlayer() {
        Lobby lobby = aLobbyHostedBy(OTHER_VALUE);
        lobby.join(aPseudonym());
        return lobby;
    }

    private static LeaveLobbyCommand leaveCommand(Lobby lobby, String playerId) {
        return new LeaveLobbyCommand(VALID_TOKEN, lobby.id().getValue(), playerId);
    }
}
