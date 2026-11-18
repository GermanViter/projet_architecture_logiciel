package com.rpg.application.lobby;

import com.rpg.domain.lobby.LobbyMember;
import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.session.exceptions.InvalidSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MAX_PLAYERS;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateLobbyUseCaseTest {
    private static final String NO_HERO_SELECTED = null;

    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    @Mock
    private LobbyRepository lobbies;

    private CreateLobbyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateLobbyUseCase(new ConnectedPlayerFinder(sessions, players), lobbies);
    }

    @Test
    void execute_connectedPlayer_returnsLobbyHostedByThatPlayer() {
        whenTokenBelongsToAPlayer();

        LobbyOutput output = useCase.execute(new CreateLobbyCommand(VALID_TOKEN));

        assertThat(output.hostId()).isEqualTo(VALID_VALUE);
        assertThat(output.status()).isEqualTo(LobbyStatus.OPEN.name());
        assertThat(output.players()).containsExactly(new LobbyMemberOutput(VALID_VALUE, VALID_VALUE, NO_HERO_SELECTED));
        assertThat(output.availableSeats()).isEqualTo(MAX_PLAYERS - 1);
        assertThat(output.id()).isNotEmpty();
    }

    @Test
    void execute_connectedPlayer_savesLobbyWithPlayerAsHostAndFirstMember() {
        whenTokenBelongsToAPlayer();

        useCase.execute(new CreateLobbyCommand(VALID_TOKEN));

        ArgumentCaptor<Lobby> saved = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbies).addLobby(saved.capture());
        assertThat(saved.getValue().host()).isEqualTo(aPseudonym());
        assertThat(saved.getValue().members()).containsExactly(new LobbyMember(aPseudonym()));
    }

    @Test
    void execute_unknownToken_isRefusedAndNothingIsSaved() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(new CreateLobbyCommand(VALID_TOKEN)));

        verifyNoInteractions(lobbies);
    }

    @Test
    void execute_missingToken_isRefusedAndNothingIsSaved() {
        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(new CreateLobbyCommand(null)));

        verifyNoInteractions(sessions, players, lobbies);
    }

    @Test
    void execute_tokenOfADeletedPlayer_isRefused() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(new CreateLobbyCommand(VALID_TOKEN)));
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }
}
