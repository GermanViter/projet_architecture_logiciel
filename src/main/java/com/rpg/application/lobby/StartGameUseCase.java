package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;

public final class StartGameUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;

    public StartGameUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
    }

    public LobbyOutput execute(StartGameCommand command) {
        Pseudonym player = connectedPlayers.find(command.token()).pseudonym();
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);

        lobby.startGame(player);

        return LobbyOutput.from(lobby);
    }
}
