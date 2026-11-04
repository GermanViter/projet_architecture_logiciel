package com.rpg.domain.hero.exceptions;



public class CharacterNameNotValidException extends RuntimeException {
    public CharacterNameNotValidException(String name) {
        super("The name \"" + name + "\" does not respect the character name rules.");
    }
}
