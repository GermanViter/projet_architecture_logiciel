package com.rpg.domain.session.exceptions;

public final class InvalidSessionException extends RuntimeException {
    public InvalidSessionException() {
        super("Missing or invalid session token.");
    }
}
