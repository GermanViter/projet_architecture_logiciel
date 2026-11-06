package com.rpg.domain.player.fixtures;

import com.rpg.domain.player.Email;

public final class EmailFixture {
    public static final String VALID_VALUE = "alice@example.com";

    private EmailFixture() {}

    public static Email anEmail() {
        return new Email(VALID_VALUE);
    }
}
