package com.rpg.domain.player.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Password must be at least 8 characters.");
    }
}
