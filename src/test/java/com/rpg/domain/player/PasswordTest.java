package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.PasswordFixture.MIN_LENGTH;
import static com.rpg.domain.player.fixtures.PasswordFixture.VALID_VALUE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordTest {

    @Test
    void password_atMinimumLength_isAccepted() {
        String atMinimum = "a".repeat(MIN_LENGTH);

        Password password = new Password(atMinimum);

        assertThat(password.getHash()).isNotEmpty();
    }

    @Test
    void password_belowMinimumLength_isRefused() {
        String belowMinimum = "a".repeat(MIN_LENGTH - 1);

        assertThrows(InvalidPasswordException.class,
                () -> new Password(belowMinimum));
    }

    @ParameterizedTest
    @NullSource
    void password_null_isRefused(String nullValue) {
        assertThrows(InvalidPasswordException.class,
                () -> new Password(nullValue));
    }

    @Test
    void password_isStoredAsHash_notPlaintext() {
        Password password = new Password(VALID_VALUE);

        assertThat(password.getHash()).isNotEqualTo(VALID_VALUE);
    }

    @Test
    void password_sameInput_producesSameHash() {
        Password first = new Password(VALID_VALUE);
        Password second = new Password(VALID_VALUE);

        assertThat(first.getHash()).isEqualTo(second.getHash());
    }

    @Test
    void verify_correctPlaintext_returnsTrue() {
        Password password = new Password(VALID_VALUE);

        assertThat(password.verify(VALID_VALUE)).isTrue();
    }

    @Test
    void verify_wrongPlaintext_returnsFalse() {
        Password password = new Password(VALID_VALUE);

        assertThat(password.verify("wrongpassword")).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void verify_null_returnsFalse(String nullValue) {
        Password password = new Password(VALID_VALUE);

        assertThat(password.verify(nullValue)).isFalse();
    }
}
