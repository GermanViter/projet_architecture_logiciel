package com.rpg.domain.player.fixtures;

import com.rpg.domain.player.Pseudonym;

public final class PseudonymFixture {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 20;
    public static final String VALID_VALUE = "Alice";
    public static final String OTHER_VALUE = "Bob";

    private PseudonymFixture() {}

    public static Pseudonym aPseudonym() {
        return new Pseudonym(VALID_VALUE);
    }

    public static Pseudonym anotherPseudonym() {
        return new Pseudonym(OTHER_VALUE);
    }
}
