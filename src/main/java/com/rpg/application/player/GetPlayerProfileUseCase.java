package com.rpg.application.player;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.exceptions.CannotViewAnotherPlayerProfileException;
import com.rpg.domain.player.exceptions.PlayerNotFoundException;

public final class GetPlayerProfileUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final PlayerRepository players;

    public GetPlayerProfileUseCase(ConnectedPlayerFinder connectedPlayers, PlayerRepository players) {
        this.connectedPlayers = connectedPlayers;
        this.players = players;
    }

    public PlayerProfileOutput execute(GetPlayerProfileCommand command) {
        Player requester = connectedPlayers.find(command.token());
        Player player = players.findById(command.playerId())
                .orElseThrow(PlayerNotFoundException::new);
        ensureViewsOwnProfile(requester, player);

        return PlayerProfileOutput.from(player);
    }

    private static void ensureViewsOwnProfile(Player requester, Player player) {
        if (!requester.pseudonym().equals(player.pseudonym())) {
            throw new CannotViewAnotherPlayerProfileException();
        }
    }
}
