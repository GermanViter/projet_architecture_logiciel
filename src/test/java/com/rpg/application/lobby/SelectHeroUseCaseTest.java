package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRepository;
import com.rpg.domain.hero.exceptions.HeroNotFoundException;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotSelectHeroForAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
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
import static com.rpg.domain.hero.fixtures.HeroFixture.HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.UNKNOWN_HERO_NAME;
import static com.rpg.domain.hero.fixtures.HeroFixture.aHero;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.UNKNOWN_LOBBY_ID;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.session.fixtures.SessionTokenFixture.VALID_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectHeroUseCaseTest {
    @Mock
    private SessionRepository sessions;

    @Mock
    private PlayerRepository players;

    @Mock
    private LobbyRepository lobbies;

    @Mock
    private HeroRepository heroes;

    private final Lobby lobbyOfThePlayer = aLobby();

    private SelectHeroUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SelectHeroUseCase(new ConnectedPlayerFinder(sessions, players), lobbies, heroes);
    }

    @Test
    void execute_memberPicksAnAvailableHero_returnsTheLobbyShowingTheirHero() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyOfThePlayer);
        whenHeroExists(aHero());

        LobbyOutput output = useCase.execute(selectCommand(VALID_VALUE, HERO_NAME));

        assertThat(output.players()).containsExactly(new LobbyMemberOutput(VALID_VALUE, VALID_VALUE, HERO_NAME));
    }

    @Test
    void execute_anotherPlayersId_isForbiddenAndNoLobbyIsLookedUp() {
        whenTokenBelongsToAPlayer();

        assertThrows(CannotSelectHeroForAnotherPlayerException.class,
                () -> useCase.execute(selectCommand(OTHER_VALUE, HERO_NAME)));

        verifyNoInteractions(lobbies, heroes);
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class, () -> useCase.execute(
                new SelectHeroCommand(VALID_TOKEN, UNKNOWN_LOBBY_ID, VALID_VALUE, HERO_NAME)));
    }

    @Test
    void execute_unknownHero_isRefusedAndNoHeroIsSelected() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored(lobbyOfThePlayer);
        when(heroes.findByName(new HeroName(UNKNOWN_HERO_NAME))).thenReturn(Optional.empty());

        assertThrows(HeroNotFoundException.class,
                () -> useCase.execute(selectCommand(VALID_VALUE, UNKNOWN_HERO_NAME)));

        assertThat(lobbyOfThePlayer.members().get(0).selectedHeroName()).isEmpty();
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class,
                () -> useCase.execute(selectCommand(VALID_VALUE, HERO_NAME)));

        verifyNoInteractions(lobbies, heroes);
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(anEmail()));
        when(players.findByEmail(anEmail())).thenReturn(Optional.of(aPlayer()));
    }

    private void whenLobbyIsStored(Lobby lobby) {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private void whenHeroExists(Hero hero) {
        when(heroes.findByName(hero.name())).thenReturn(Optional.of(hero));
    }

    private SelectHeroCommand selectCommand(String playerId, String heroName) {
        return new SelectHeroCommand(VALID_TOKEN, lobbyOfThePlayer.id().getValue(), playerId, heroName);
    }
}
