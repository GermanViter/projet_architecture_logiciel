package com.rpg.domain.player.exceptions;

public abstract class PlayerException extends RuntimeException {
    private final String code;

    protected PlayerException(String code, String description) {
        super(description);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
