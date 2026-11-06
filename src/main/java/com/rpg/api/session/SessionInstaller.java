package com.rpg.api.session;

import com.rpg.application.session.LoginUseCase;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.infrastructure.session.SessionRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class SessionInstaller {
    private final PlayerRepository players;

    public SessionInstaller(PlayerRepository players) {
        this.players = players;
    }

    public void install(ResourceConfig config) {
        SessionRegistry sessionRegistry = new SessionRegistry();
        LoginUseCase login = new LoginUseCase(players, sessionRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(login).to(LoginUseCase.class);
            }
        });
        config.register(SessionResource.class);
        config.register(new InvalidCredentialsExceptionMapper());
    }
}
