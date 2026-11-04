package com.rpg.domain.player.fixtures;

import com.rpg.domain.player.Password;

public final class PasswordFixture {
    public static final int MIN_LENGTH = 8;
    public static final String VALID_VALUE = "secret123";

    private PasswordFixture() {}

    public static Password aPassword() {
        return new Password(VALID_VALUE);
    }
}
