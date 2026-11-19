package com.rpg.api;

import com.rpg.domain.session.exceptions.InvalidSessionException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BearerTokenTest {

    private static final String TOKEN = "AbC-123-xYz";

    @Test
    void extractFrom_bearerScheme_returnsTheToken() {
        assertThat(BearerToken.extractFrom("Bearer " + TOKEN)).isEqualTo(TOKEN);
    }

    @Test
    void extractFrom_lowercaseScheme_returnsTheTokenWithItsCaseKept() {
        assertThat(BearerToken.extractFrom("bearer " + TOKEN)).isEqualTo(TOKEN);
    }

    @Test
    void extractFrom_uppercaseScheme_returnsTheTokenWithItsCaseKept() {
        assertThat(BearerToken.extractFrom("BEARER " + TOKEN)).isEqualTo(TOKEN);
    }

    @Test
    void extractFrom_otherScheme_isRejected() {
        assertThrows(InvalidSessionException.class,
                () -> BearerToken.extractFrom("Basic " + TOKEN));
    }

    @Test
    void extractFrom_schemeWithoutSeparatingSpace_isRejected() {
        assertThrows(InvalidSessionException.class,
                () -> BearerToken.extractFrom("Bearer" + TOKEN));
    }

    @Test
    void extractFrom_missingHeader_isRejected() {
        assertThrows(InvalidSessionException.class,
                () -> BearerToken.extractFrom(null));
    }
}
