package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.session.exceptions.InvalidSessionException;

public final class CreateLobbyUseCase {
    private final SessionRepository sessions;
    private final PlayerRepository players;
    private final LobbyRepository lobbies;

    public CreateLobbyUseCase(SessionRepository sessions, PlayerRepository players,
                              LobbyRepository lobbies) {
        this.sessions = sessions;
        this.players = players;
        this.lobbies = lobbies;
    }

    public CreateLobbyOutput execute(CreateLobbyCommand command) {
        Player host = findConnectedPlayer(command.token());

        Lobby lobby = Lobby.openedBy(host.pseudonym());
        lobbies.addLobby(lobby);

        return new CreateLobbyOutput(lobby.id().getValue(), host.pseudonym().getValue());
    }

    private Player findConnectedPlayer(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidSessionException();
        }
        Email email = sessions.findEmailByToken(SessionToken.of(token))
                .orElseThrow(InvalidSessionException::new);
        return players.findByEmail(email).orElseThrow(InvalidSessionException::new);
    }
}
