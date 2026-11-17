package com.rpg.application.player;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.exceptions.CannotViewAnotherPlayerProfileException;
import com.rpg.domain.player.exceptions.PlayerNotFoundException;
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
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayerWithEmail;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.THIRD_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPlayerProfileUseCaseTest {
    private static final String ANOTHER_PLAYERS_EMAIL = "bob@example.com";

    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    private final Player connectedPlayer = aPlayer();

    private GetPlayerProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetPlayerProfileUseCase(new ConnectedPlayerFinder(sessions, players), players);
    }

    @Test
    void execute_ownProfile_returnsPseudonymAndEmail() {
        whenTokenBelongsToTheConnectedPlayer();
        when(players.findById(VALID_VALUE)).thenReturn(Optional.of(connectedPlayer));

        PlayerProfileOutput output = useCase.execute(new GetPlayerProfileCommand(VALID_TOKEN, VALID_VALUE));

        assertThat(output.id()).isEqualTo(VALID_VALUE);
        assertThat(output.pseudonym()).isEqualTo(VALID_VALUE);
        assertThat(output.email()).isEqualTo(anEmail().getValue());
    }

    @Test
    void execute_anotherPlayersProfile_isForbidden() {
        whenTokenBelongsToTheConnectedPlayer();
        when(players.findById(OTHER_VALUE)).thenReturn(Optional.of(aPlayerWithEmail(ANOTHER_PLAYERS_EMAIL)));

        assertThrows(CannotViewAnotherPlayerProfileException.class,
                () -> useCase.execute(new GetPlayerProfileCommand(VALID_TOKEN, OTHER_VALUE)));
    }

    @Test
    void execute_unknownPlayer_isNotFound() {
        whenTokenBelongsToTheConnectedPlayer();
        when(players.findById(THIRD_VALUE)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
                () -> useCase.execute(new GetPlayerProfileCommand(VALID_TOKEN, THIRD_VALUE)));
    }

    @Test
    void execute_unknownToken_isRefusedBeforeAnyPlayerIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(new GetPlayerProfileCommand(VALID_TOKEN, VALID_VALUE)));

        verifyNoInteractions(players);
    }

    private void whenTokenBelongsToTheConnectedPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(connectedPlayer));
    }
}
