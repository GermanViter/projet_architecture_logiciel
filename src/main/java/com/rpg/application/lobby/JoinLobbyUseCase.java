package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;

public final class JoinLobbyUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;

    public JoinLobbyUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
    }

    public JoinLobbyOutput execute(JoinLobbyCommand command) {
        Pseudonym newcomer = connectedPlayers.find(command.token()).pseudonym();
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);

        lobbies.joinLobby(lobby, newcomer);

        return new JoinLobbyOutput(newcomer.getValue(), LobbyOutput.from(lobby));
    }
}
