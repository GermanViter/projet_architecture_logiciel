package com.rpg.infrastructure.session;

import com.rpg.domain.session.SessionToken;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.EmailFixture.anEmail;

class SessionRegistryTest {

    private final SessionRegistry registry = new SessionRegistry();

    @Test
    void findEmailByToken_knownToken_returnsTheEmail() {
        SessionToken token = new SessionToken();
        registry.addSession(token, anEmail());

        assertThat(registry.findEmailByToken(token)).hasValue(anEmail());
    }

    @Test
    void findEmailByToken_tokenRebuiltFromItsValue_returnsTheEmail() {
        SessionToken token = new SessionToken();
        registry.addSession(token, anEmail());

        assertThat(registry.findEmailByToken(SessionToken.of(token.getValue())))
                .hasValue(anEmail());
    }

    @Test
    void findEmailByToken_unknownToken_returnsEmpty() {
        assertThat(registry.findEmailByToken(new SessionToken())).isEmpty();
    }
}
