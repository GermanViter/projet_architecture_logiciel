package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.application.player.GetPlayerProfileUseCase;
import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class PlayerInstaller {
    private final PlayerRepository players;
    private final SessionRepository sessions;

    public PlayerInstaller(PlayerRepository players, SessionRepository sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public void install(ResourceConfig config) {
        ConnectedPlayerFinder connectedPlayers = new ConnectedPlayerFinder(sessions, players);
        CreatePlayerUseCase createPlayer = new CreatePlayerUseCase(players);
        GetPlayerProfileUseCase getPlayerProfile = new GetPlayerProfileUseCase(connectedPlayers, players);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createPlayer).to(CreatePlayerUseCase.class);
                bind(getPlayerProfile).to(GetPlayerProfileUseCase.class);
            }
        });
        config.register(PlayerResource.class);
        config.register(new DuplicatePlayerExceptionMapper());
        config.register(new InvalidPseudonymExceptionMapper());
        config.register(new InvalidEmailExceptionMapper());
        config.register(new InvalidPasswordExceptionMapper());
        config.register(new PlayerNotFoundExceptionMapper());
        config.register(new CannotViewAnotherPlayerProfileExceptionMapper());
    }
}
