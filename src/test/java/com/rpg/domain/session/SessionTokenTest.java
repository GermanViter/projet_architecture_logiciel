package com.rpg.domain.session;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class SessionTokenTest {

    @Test
    void newTokens_areDistinct() {
        assertThat(new SessionToken()).isNotEqualTo(new SessionToken());
    }

    @Test
    void of_sameValue_isEqual() {
        assertThat(SessionToken.of("abc")).isEqualTo(SessionToken.of("abc"));
    }

    @Test
    void of_aTokenValue_rebuildsTheSameToken() {
        SessionToken token = new SessionToken();

        assertThat(SessionToken.of(token.getValue())).isEqualTo(token);
    }
}
