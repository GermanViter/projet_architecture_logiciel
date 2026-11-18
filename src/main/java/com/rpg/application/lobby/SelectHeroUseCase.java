package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.hero.HeroRegistry;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotRemoveAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.HeroNotFoundException;
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
        ensureSelectsOwnHero(player, command.playerId());
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);
        Hero hero = heroRegistry.findByName(new HeroName(command.heroName()))
                .orElseThrow(HeroNotFoundException::new);

        lobby.selectHero(player, hero);

        return LobbyOutput.from(lobby);
    }

    private static void ensureSelectsOwnHero(Pseudonym player, String playerId) {
        if (!player.getValue().equals(playerId)) {
            throw new CannotRemoveAnotherPlayerException();
        }
    }
}
