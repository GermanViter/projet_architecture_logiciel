package com.rpg.api.lobby;

import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.application.lobby.JoinLobbyUseCase;
import com.rpg.application.lobby.LeaveLobbyUseCase;
import com.rpg.application.lobby.SelectHeroUseCase;
import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.HeroFactory;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.hero.characteristics.Characteristic;
import com.rpg.domain.hero.characteristics.Characteristics;
import com.rpg.domain.hero.role.Warrior;
import com.rpg.domain.hero.species.Species;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.infrastructure.lobby.LobbyRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.List;

public final class LobbyInstaller {
    private final PlayerRepository players;
    private final SessionRepository sessions;

    public LobbyInstaller(PlayerRepository players, SessionRepository sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public void install(ResourceConfig config) {
        LobbyRegistry lobbyRegistry = new LobbyRegistry();
        HeroRegistry heroRegistry = new HeroRegistry();
        HeroFactory heroFactory = new HeroFactory();
        heroRegistry.addHero(heroFactory.create(
                new HeroName("Gandalf"), Species.HUMAN, new Warrior(),
                List.of(new Characteristic(Characteristics.STRENGTH, 10),
                        new Characteristic(Characteristics.CONSTITUTION, 10))));
        heroRegistry.addHero(heroFactory.create(
                new HeroName("Aragorn"), Species.HUMAN, new Warrior(),
                List.of(new Characteristic(Characteristics.STRENGTH, 10),
                        new Characteristic(Characteristics.CONSTITUTION, 10))));
        heroRegistry.addHero(heroFactory.create(
                new HeroName("Legolas"), Species.ELF, new Warrior(),
                List.of(new Characteristic(Characteristics.STRENGTH, 10),
                        new Characteristic(Characteristics.CONSTITUTION, 10))));
        heroRegistry.addHero(heroFactory.create(
                new HeroName("Gimli"), Species.DWARF, new Warrior(),
                List.of(new Characteristic(Characteristics.STRENGTH, 10),
                        new Characteristic(Characteristics.CONSTITUTION, 10))));
        ConnectedPlayerFinder connectedPlayers = new ConnectedPlayerFinder(sessions, players);
        CreateLobbyUseCase createLobby = new CreateLobbyUseCase(connectedPlayers, lobbyRegistry);
        JoinLobbyUseCase joinLobby = new JoinLobbyUseCase(connectedPlayers, lobbyRegistry);
        LeaveLobbyUseCase leaveLobby = new LeaveLobbyUseCase(connectedPlayers, lobbyRegistry);
        SelectHeroUseCase selectHero = new SelectHeroUseCase(connectedPlayers, lobbyRegistry, heroRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createLobby).to(CreateLobbyUseCase.class);
                bind(joinLobby).to(JoinLobbyUseCase.class);
                bind(leaveLobby).to(LeaveLobbyUseCase.class);
                bind(selectHero).to(SelectHeroUseCase.class);
            }
        });
        config.register(LobbyResource.class);
        config.register(new LobbyNotFoundExceptionMapper());
        config.register(new LobbyExceptionMapper());
        config.register(new CannotRemoveAnotherPlayerExceptionMapper());
        config.register(new PlayerNotInLobbyExceptionMapper());
        config.register(new HeroNotFoundExceptionMapper());
    }
}
