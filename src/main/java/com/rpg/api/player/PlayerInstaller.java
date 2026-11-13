
package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.infrastructure.player.PlayerRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class PlayerInstaller {
    private final PlayerRegistry playerRegistry;

    public PlayerInstaller(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    public void install(ResourceConfig config) {
        CreatePlayerUseCase createPlayer = new CreatePlayerUseCase(playerRegistry);
        CreateLobbyUseCase createLobby = new CreateLobbyUseCase(playerRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createPlayer).to(CreatePlayerUseCase.class);
                bind(createLobby).to(CreateLobbyUseCase.class);
            }
        });

        config.register(PlayerResource.class);
        config.register(new DuplicatePlayerExceptionMapper());
        config.register(new InvalidPseudonymExceptionMapper());
        config.register(new InvalidEmailExceptionMapper());
        config.register(new InvalidPasswordExceptionMapper());
        config.register(LobbyResource.class);
    }
}
