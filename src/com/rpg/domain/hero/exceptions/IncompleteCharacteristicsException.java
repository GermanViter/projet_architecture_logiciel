package com.rpg.domain.hero.exceptions;



public class IncompleteCharacteristicsException extends RuntimeException {
    public IncompleteCharacteristicsException() {
        super("The characteristics are not complete. All 6 characteristic values (Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma) must be defined.");
    }
}
