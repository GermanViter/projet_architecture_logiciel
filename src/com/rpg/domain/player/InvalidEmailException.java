package com.rpg.domain.player;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super("Email \"" + email + "\" is not valid.");
    }
}
