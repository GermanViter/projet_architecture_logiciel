package com.rpg.application.session;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.session.exceptions.InvalidSessionException;

public final class ConnectedPlayerFinder {
    private final SessionRepository sessions;
    private final PlayerRepository players;

    public ConnectedPlayerFinder(SessionRepository sessions, PlayerRepository players) {
        this.sessions = sessions;
        this.players = players;
    }

    public Player find(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidSessionException();
        }
        Email email = sessions.findEmailByToken(SessionToken.of(token))
                .orElseThrow(InvalidSessionException::new);
        return players.findByEmail(email).orElseThrow(InvalidSessionException::new);
    }
}
