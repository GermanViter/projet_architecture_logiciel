package com.rpg.infrastructure.player;

import com.rpg.domain.player.exceptions.DuplicatePlayerException;
import org.junit.jupiter.api.Test;

import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayerWithEmail;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayerWithPseudonym;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerRegistryTest {

    private final PlayerRegistry registry = new PlayerRegistry();

    @Test
    void addPlayer_validPlayer_isStored() {
        registry.addPlayer(aPlayer());

        assertThat(registry.allPlayers()).hasSize(1);
    }

    @Test
    void addPlayer_duplicatePseudonym_isRefused() {
        registry.addPlayer(aPlayer());

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(aPlayerWithPseudonym(VALID_VALUE)));
    }

    @Test
    void addPlayer_duplicateEmail_isRefused() {
        registry.addPlayer(aPlayer());

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(aPlayerWithEmail("alice@example.com")));
    }

    @Test
    void addPlayer_duplicatePseudonym_doesNotSaveIncompleteState() {
        registry.addPlayer(aPlayer());

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(aPlayerWithPseudonym(VALID_VALUE)));

        assertThat(registry.allPlayers()).hasSize(1);
    }

    @Test
    void addPlayer_twoDifferentPlayers_bothAreStored() {
        registry.addPlayer(aPlayer());
        registry.addPlayer(aPlayerWithEmail("bob@example.com"));

        assertThat(registry.allPlayers()).hasSize(2);
    }
}
