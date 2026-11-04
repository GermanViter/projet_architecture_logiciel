package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.EmailFixture.VALID_VALUE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void email_validFormat_isAccepted() {
        Email email = new Email(VALID_VALUE);

        assertThat(email.getValue()).isEqualTo(VALID_VALUE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"notanemail", "missing@dot", "@nodomain.com", "no-at-sign.com", "spaces in@email.com"})
    void email_invalidFormat_isRefused(String invalid) {
        assertThrows(InvalidEmailException.class,
                () -> new Email(invalid));
    }

    @ParameterizedTest
    @NullSource
    void email_null_isRefused(String nullValue) {
        assertThrows(InvalidEmailException.class,
                () -> new Email(nullValue));
    }

    @Test
    void email_sameAddress_areEqual() {
        Email first = new Email(VALID_VALUE);
        Email second = new Email(VALID_VALUE);

        assertThat(first.getLowerCase()).isEqualTo(second.getLowerCase());
    }

    @Test
    void email_differentAddresses_areNotEqual() {
        Email first = new Email(VALID_VALUE);
        Email second = new Email("bob@example.com");

        assertThat(first).isNotEqualTo(second);
    }
}
