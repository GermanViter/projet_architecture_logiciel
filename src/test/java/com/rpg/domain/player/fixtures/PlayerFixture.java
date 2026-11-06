package com.rpg.domain.player.fixtures;

import com.rpg.domain.player.Player;

public final class PlayerFixture {
    private PlayerFixture() {}

    public static Player aPlayer() {
        return new Player(
                PseudonymFixture.aPseudonym(),
                EmailFixture.anEmail(),
                PasswordFixture.aPassword());
    }

    public static Player aPlayerWithPseudonym(String pseudonym) {
        return new Player(
                new com.rpg.domain.player.Pseudonym(pseudonym),
                EmailFixture.anEmail(),
                PasswordFixture.aPassword());
    }

    public static Player aPlayerWithEmail(String email) {
        return new Player(
                new com.rpg.domain.player.Pseudonym("Bob"),
                new com.rpg.domain.player.Email(email),
                PasswordFixture.aPassword());
    }
}
