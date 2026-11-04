package com.rpg.domain.player.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super("Email \"" + email + "\" is not valid.");
    }
}
