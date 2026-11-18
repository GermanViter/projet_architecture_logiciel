package com.rpg.api.lobby;

import com.rpg.api.hero.CharacterNameNotValidExceptionMapper;
import com.rpg.api.hero.HeroNotFoundExceptionMapper;
import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.application.lobby.GetLobbyDetailsUseCase;
import com.rpg.application.lobby.JoinLobbyUseCase;
import com.rpg.application.lobby.LeaveLobbyUseCase;
import com.rpg.application.lobby.ListLobbiesUseCase;
import com.rpg.application.lobby.SelectHeroUseCase;
import com.rpg.application.lobby.StartGameUseCase;
import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.HeroRepository;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.infrastructure.lobby.LobbyRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class LobbyInstaller {
    private final PlayerRepository players;
    private final SessionRepository sessions;
    private final HeroRepository heroes;

    public LobbyInstaller(PlayerRepository players, SessionRepository sessions, HeroRepository heroes) {
        this.players = players;
        this.sessions = sessions;
        this.heroes = heroes;
    }

    public void install(ResourceConfig config) {
        LobbyRegistry lobbyRegistry = new LobbyRegistry();
        ConnectedPlayerFinder connectedPlayers = new ConnectedPlayerFinder(sessions, players);
        CreateLobbyUseCase createLobby = new CreateLobbyUseCase(connectedPlayers, lobbyRegistry);
        JoinLobbyUseCase joinLobby = new JoinLobbyUseCase(connectedPlayers, lobbyRegistry);
        LeaveLobbyUseCase leaveLobby = new LeaveLobbyUseCase(connectedPlayers, lobbyRegistry);
        ListLobbiesUseCase listLobbies = new ListLobbiesUseCase(lobbyRegistry);
        GetLobbyDetailsUseCase getLobbyDetails = new GetLobbyDetailsUseCase(lobbyRegistry);
        SelectHeroUseCase selectHero = new SelectHeroUseCase(connectedPlayers, lobbyRegistry, heroes);
        StartGameUseCase startGame = new StartGameUseCase(connectedPlayers, lobbyRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createLobby).to(CreateLobbyUseCase.class);
                bind(joinLobby).to(JoinLobbyUseCase.class);
                bind(leaveLobby).to(LeaveLobbyUseCase.class);
                bind(listLobbies).to(ListLobbiesUseCase.class);
                bind(getLobbyDetails).to(GetLobbyDetailsUseCase.class);
                bind(selectHero).to(SelectHeroUseCase.class);
                bind(startGame).to(StartGameUseCase.class);
            }
        });
        config.register(LobbyResource.class);
        config.register(new LobbyNotFoundExceptionMapper());
        config.register(new LobbyExceptionMapper());
        config.register(new CannotRemoveAnotherPlayerExceptionMapper());
        config.register(new PlayerNotInLobbyExceptionMapper());
        config.register(new CannotSelectHeroForAnotherPlayerExceptionMapper());
        config.register(new HeroNotFoundExceptionMapper());
        config.register(new CharacterNameNotValidExceptionMapper());
        config.register(new NotHostExceptionMapper());
    }
}
