package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.fixtures.HeroFixture;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.species.Species;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotRemoveAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.GameAlreadyStartedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadySelectedException;
import com.rpg.domain.lobby.exceptions.HeroAlreadyTakenException;
import com.rpg.domain.lobby.exceptions.HeroNotFoundException;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.lobby.fixtures.LobbyFixture;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.fixtures.EmailFixture;
import com.rpg.domain.player.fixtures.PlayerFixture;
import com.rpg.domain.player.fixtures.PseudonymFixture;
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
import static com.rpg.domain.hero.fixtures.HeroFixture.VALID_HERO_NAME;
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

    private SelectHeroUseCase useCase;

    private final Lobby lobby = LobbyFixture.aLobby();
    private final HeroRegistry heroRegistry = new HeroRegistry();
    private final Hero hero = HeroFixture.aHero();

    @BeforeEach
    void setUp() {
        heroRegistry.addHero(hero);
        useCase = new SelectHeroUseCase(new ConnectedPlayerFinder(sessions, players), lobbies, heroRegistry);
    }

    @Test
    void execute_memberSelectsHero_returnsUpdatedLobby() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored();

        LobbyOutput output = useCase.execute(selectHeroCommand());

        assertThat(output.players()).hasSize(1);
        assertThat(output.players().get(0).selectedHero()).isEqualTo(VALID_HERO_NAME);
    }

    @Test
    void execute_unknownLobby_isRefused() {
        whenTokenBelongsToAPlayer();
        when(lobbies.findById(any())).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class, () -> useCase.execute(selectHeroCommand()));
    }

    @Test
    void execute_unknownToken_isRefusedAndNoLobbyIsLookedUp() {
        when(sessions.findEmailByToken(any())).thenReturn(Optional.empty());

        assertThrows(InvalidSessionException.class, () -> useCase.execute(selectHeroCommand()));

        verifyNoInteractions(lobbies);
    }

    @Test
    void execute_playerAlreadySelectedHero_isRefused() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored();
        lobby.selectHero(PseudonymFixture.aPseudonym(), hero);

        assertThrows(HeroAlreadySelectedException.class, () -> useCase.execute(selectHeroCommand()));
    }

    @Test
    void execute_heroAlreadyTakenByAnotherPlayer_isRefused() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored();
        lobby.join(PseudonymFixture.anotherPseudonym());
        lobby.selectHero(PseudonymFixture.anotherPseudonym(), hero);

        assertThrows(HeroAlreadyTakenException.class, () -> useCase.execute(selectHeroCommand()));
    }

    @Test
    void execute_gameAlreadyStarted_isRefused() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored();
        lobby.startGame();

        assertThrows(GameAlreadyStartedException.class, () -> useCase.execute(selectHeroCommand()));
    }

    @Test
    void execute_heroNotFound_isRefused() {
        whenTokenBelongsToAPlayer();
        whenLobbyIsStored();

        assertThrows(HeroNotFoundException.class, () -> useCase.execute(
                new SelectHeroCommand(VALID_TOKEN, lobby.id().getValue(), VALID_VALUE, "NonExistent")));
    }

    @Test
    void execute_playerIdDoesNotMatchToken_isRefused() {
        whenTokenBelongsToAPlayer();

        assertThrows(CannotRemoveAnotherPlayerException.class, () -> useCase.execute(
                new SelectHeroCommand(VALID_TOKEN, lobby.id().getValue(), "otherPlayer", VALID_HERO_NAME)));
    }

    private void whenTokenBelongsToAPlayer() {
        when(sessions.findEmailByToken(SessionToken.of(VALID_TOKEN))).thenReturn(Optional.of(EmailFixture.anEmail()));
        when(players.findByEmail(EmailFixture.anEmail())).thenReturn(Optional.of(PlayerFixture.aPlayer()));
    }

    private void whenLobbyIsStored() {
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));
    }

    private SelectHeroCommand selectHeroCommand() {
        return new SelectHeroCommand(VALID_TOKEN, lobby.id().getValue(), VALID_VALUE, VALID_HERO_NAME);
    }
}
