package com.rpg.domain.session;

import com.rpg.domain.player.Email;

import java.util.Optional;

public interface SessionRepository {
    void addSession(SessionToken token, Email email);
    Optional<Email> findEmailByToken(SessionToken token);
}
