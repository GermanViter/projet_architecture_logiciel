package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.player.Player;

public final class CreateLobbyUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;

    public CreateLobbyUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
    }

    public CreateLobbyOutput execute(CreateLobbyCommand command) {
        Player host = connectedPlayers.find(command.token());

        Lobby lobby = Lobby.openedBy(host.pseudonym());
        lobbies.addLobby(lobby);

        return new CreateLobbyOutput(lobby.id().getValue(), host.pseudonym().getValue());
    }
}
