package com.rpg.domain.player;

public class InvalidPseudonymException extends RuntimeException {
    public InvalidPseudonymException(String pseudonym) {
        super("Pseudonym \"" + pseudonym + "\" is invalid. Must be 3-20 characters.");
    }
}