package com.rpg.domain.player.exceptions;

public class ForbiddenException extends PlayerException {
    public ForbiddenException() {
        super("FORBIDDEN", "Access to this profile is forbidden.");
    }
}
