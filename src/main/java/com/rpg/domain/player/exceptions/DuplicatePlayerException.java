package com.rpg.domain.player.exceptions;

public class DuplicatePlayerException extends RuntimeException {
    public DuplicatePlayerException(String field, String value) {
        super("A player with " + field + " \"" + value + "\" already exists.");
    }
}
