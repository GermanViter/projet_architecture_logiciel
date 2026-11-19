package com.rpg.application.session;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.exceptions.InvalidCredentialsException;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;

public final class LoginUseCase {
    private final PlayerRepository players;
    private final SessionRepository sessions;

    public LoginUseCase(PlayerRepository players, SessionRepository sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public LoginOutput execute(LoginCommand command) {
        Email email = parseEmailOrFail(command.email());
        Player player = players.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!player.password().verify(command.password())) {
            throw new InvalidCredentialsException();
        }

        SessionToken token = new SessionToken();
        sessions.addSession(token, email);

        return new LoginOutput(token.getValue());
    }

    private Email parseEmailOrFail(String raw) {
        try {
            return new Email(raw);
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }
}
