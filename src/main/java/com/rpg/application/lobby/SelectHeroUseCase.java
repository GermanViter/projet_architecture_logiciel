package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;

public final class SelectHeroUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;
    private final HeroRegistry heroRegistry;

    public SelectHeroUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies,
                             HeroRegistry heroRegistry) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
        this.heroRegistry = heroRegistry;
    }

    public LobbyOutput execute(SelectHeroCommand command) {
        Pseudonym player = connectedPlayers.find(command.token()).pseudonym();
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);

        Hero hero = findHeroByName(command.heroName());
        lobby.selectHero(player, hero);
        lobbies.save(lobby);

        return LobbyOutput.from(lobby);
    }

    private Hero findHeroByName(String heroName) {
        return heroRegistry.allHeroes().stream()
                .filter(h -> h.name().getName().equals(heroName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hero not found: " + heroName));
    }
}
