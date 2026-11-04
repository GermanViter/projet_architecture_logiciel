package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.infrastructure.player.PlayerRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class PlayerInstaller {
    public void install(ResourceConfig config) {
        PlayerRegistry playerRegistry = new PlayerRegistry();
        CreatePlayerUseCase createPlayer = new CreatePlayerUseCase(playerRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createPlayer).to(CreatePlayerUseCase.class);
            }
        });
        config.register(PlayerResource.class);
        config.register(new DuplicatePlayerExceptionMapper());
        config.register(new InvalidPseudonymExceptionMapper());
        config.register(new InvalidEmailExceptionMapper());
        config.register(new InvalidPasswordExceptionMapper());
    }
}
