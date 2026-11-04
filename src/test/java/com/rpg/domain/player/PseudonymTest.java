package com.rpg.domain.player;

import com.rpg.domain.player.exceptions.InvalidPseudonymException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.PseudonymFixture.MAX_LENGTH;
import static com.rpg.domain.player.fixtures.PseudonymFixture.MIN_LENGTH;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PseudonymTest {

    @Test
    void pseudonym_atMinimumLength_isAccepted() {
        String atMinimum = "a".repeat(MIN_LENGTH);

        Pseudonym pseudonym = new Pseudonym(atMinimum);

        assertThat(pseudonym.getValue()).isEqualTo(atMinimum);
    }

    @Test
    void pseudonym_belowMinimumLength_isRefused() {
        String belowMinimum = "a".repeat(MIN_LENGTH - 1);

        assertThrows(InvalidPseudonymException.class,
                () -> new Pseudonym(belowMinimum));
    }

    @Test
    void pseudonym_atMaximumLength_isAccepted() {
        String atMaximum = "a".repeat(MAX_LENGTH);

        Pseudonym pseudonym = new Pseudonym(atMaximum);

        assertThat(pseudonym.getValue()).isEqualTo(atMaximum);
    }

    @Test
    void pseudonym_aboveMaximumLength_isRefused() {
        String aboveMaximum = "a".repeat(MAX_LENGTH + 1);

        assertThrows(InvalidPseudonymException.class,
                () -> new Pseudonym(aboveMaximum));
    }

    @ParameterizedTest
    @NullSource
    void pseudonym_null_isRefused(String nullValue) {
        assertThrows(InvalidPseudonymException.class,
                () -> new Pseudonym(nullValue));
    }

    @Test
    void pseudonym_validValue_returnsValue() {
        Pseudonym pseudonym = new Pseudonym(VALID_VALUE);

        assertThat(pseudonym.getValue()).isEqualTo(VALID_VALUE);
    }

    @Test
    void pseudonym_sameName_areEqual() {
        Pseudonym first = new Pseudonym(VALID_VALUE);
        Pseudonym second = new Pseudonym(VALID_VALUE);

        assertThat(first).isEqualTo(second);
    }

    @Test
    void pseudonym_differentNames_areNotEqual() {
        Pseudonym first = new Pseudonym(VALID_VALUE);
        Pseudonym second = new Pseudonym("Bob");

        assertThat(first).isNotEqualTo(second);
    }
}
