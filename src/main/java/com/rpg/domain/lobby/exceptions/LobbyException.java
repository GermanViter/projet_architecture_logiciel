package com.rpg.domain.lobby.exceptions;

public abstract class LobbyException extends RuntimeException {
    private final String code;

    protected LobbyException(String code, String description) {
        super(description);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
