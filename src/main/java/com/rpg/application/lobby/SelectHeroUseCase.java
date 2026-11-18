package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRepository;
import com.rpg.domain.hero.exceptions.HeroNotFoundException;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotSelectHeroForAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;

public final class SelectHeroUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;
    private final HeroRepository heroes;

    public SelectHeroUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies,
                             HeroRepository heroes) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
        this.heroes = heroes;
    }

    public LobbyOutput execute(SelectHeroCommand command) {
        Pseudonym selector = connectedPlayers.find(command.token()).pseudonym();
        ensureSelectsForThemselves(selector, command.playerId());
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);
        Hero hero = findHero(new HeroName(command.heroName()));

        lobby.selectHero(selector, hero);

        return LobbyOutput.from(lobby);
    }

    private Hero findHero(HeroName name) {
        return heroes.findByName(name)
                .orElseThrow(() -> new HeroNotFoundException(name));
    }

    private static void ensureSelectsForThemselves(Pseudonym selector, String playerId) {
        if (!selector.getValue().equals(playerId)) {
            throw new CannotSelectHeroForAnotherPlayerException();
        }
    }
}
