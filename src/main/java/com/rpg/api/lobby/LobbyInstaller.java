package com.rpg.api.lobby;

import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.infrastructure.lobby.LobbyRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class LobbyInstaller {
    private final PlayerRepository players;
    private final SessionRepository sessions;

    public LobbyInstaller(PlayerRepository players, SessionRepository sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public void install(ResourceConfig config) {
        LobbyRegistry lobbyRegistry = new LobbyRegistry();
        CreateLobbyUseCase createLobby = new CreateLobbyUseCase(sessions, players, lobbyRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createLobby).to(CreateLobbyUseCase.class);
            }
        });
        config.register(LobbyResource.class);
        config.register(new PlayerAlreadyInLobbyExceptionMapper());
    }
}
