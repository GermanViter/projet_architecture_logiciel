package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.application.player.GetPlayerProfileUseCase;
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
        GetPlayerProfileUseCase getPlayerProfile = new GetPlayerProfileUseCase(playerRegistry);

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
        config.register(new ForbiddenExceptionMapper());
        config.register(new PlayerNotFoundExceptionMapper());
    }
}
