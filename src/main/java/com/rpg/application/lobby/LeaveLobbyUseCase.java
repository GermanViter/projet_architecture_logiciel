package com.rpg.application.lobby;

import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.exceptions.CannotRemoveAnotherPlayerException;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import com.rpg.domain.player.Pseudonym;

public final class LeaveLobbyUseCase {
    private final ConnectedPlayerFinder connectedPlayers;
    private final LobbyRepository lobbies;

    public LeaveLobbyUseCase(ConnectedPlayerFinder connectedPlayers, LobbyRepository lobbies) {
        this.connectedPlayers = connectedPlayers;
        this.lobbies = lobbies;
    }

    public void execute(LeaveLobbyCommand command) {
        Pseudonym leaver = connectedPlayers.find(command.token()).pseudonym();
        ensureLeavesThemselves(leaver, command.playerId());
        Lobby lobby = lobbies.findById(LobbyId.of(command.lobbyId()))
                .orElseThrow(LobbyNotFoundException::new);

        lobby.leave(leaver);
        if (lobby.isEmpty()) {
            lobbies.removeLobby(lobby);
        }
    }

    private static void ensureLeavesThemselves(Pseudonym leaver, String playerId) {
        if (!leaver.getValue().equals(playerId)) {
            throw new CannotRemoveAnotherPlayerException();
        }
    }
}
