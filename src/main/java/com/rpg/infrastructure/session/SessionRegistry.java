package com.rpg.infrastructure.session;

import com.rpg.domain.player.Email;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// ponytail: HashMap not thread-safe — ConcurrentHashMap if concurrent requests matter
public final class SessionRegistry implements SessionRepository {
    private final Map<SessionToken, Email> sessions = new HashMap<>();

    @Override
    public void addSession(SessionToken token, Email email) {
        sessions.put(token, email);
    }

    @Override
    public Optional<Email> findEmailByToken(SessionToken token) {
        return Optional.ofNullable(sessions.get(token));
    }
}
