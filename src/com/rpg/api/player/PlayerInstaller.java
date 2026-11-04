package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.domain.player.PlayerRegistry;
import org.glassfish.jersey.server.ResourceConfig;

public final class PlayerInstaller {
    public void install(ResourceConfig config) {
        PlayerRegistry playerRegistry = new PlayerRegistry();
        CreatePlayerUseCase createPlayer = new CreatePlayerUseCase(playerRegistry);

        config.register(new PlayerResource(createPlayer));
        config.register(new DuplicatePlayerExceptionMapper());
        config.register(new InvalidPseudonymExceptionMapper());
        config.register(new InvalidEmailExceptionMapper());
        config.register(new InvalidPasswordExceptionMapper());
    }
}
