package com.rpg.domain.player;

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
        Player player = aPlayer();

        registry.addPlayer(player);

        assertThat(registry.allPlayers()).containsExactly(player);
    }

    @Test
    void addPlayer_duplicatePseudonym_isRefused() {
        registry.addPlayer(aPlayer());
        Player duplicate = aPlayerWithPseudonym(VALID_VALUE);

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(duplicate));
    }

    @Test
    void addPlayer_duplicateEmail_isRefused() {
        registry.addPlayer(aPlayer());
        Player sameEmail = aPlayerWithEmail("alice@example.com");

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(sameEmail));
    }

    @Test
    void addPlayer_duplicatePseudonym_doesNotSaveIncompleteState() {
        registry.addPlayer(aPlayer());
        Player duplicate = aPlayerWithPseudonym(VALID_VALUE);

        assertThrows(DuplicatePlayerException.class,
                () -> registry.addPlayer(duplicate));

        assertThat(registry.allPlayers()).hasSize(1);
    }

    @Test
    void addPlayer_twoDifferentPlayers_bothAreStored() {
        Player first = aPlayer();
        Player second = aPlayerWithEmail("bob@example.com");

        registry.addPlayer(first);
        registry.addPlayer(second);

        assertThat(registry.allPlayers()).hasSize(2);
    }
}
