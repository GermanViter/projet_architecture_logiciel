package com.rpg.api;

import com.rpg.domain.session.exceptions.InvalidSessionException;

public final class BearerToken {
    private static final String BEARER_PREFIX = "Bearer ";

    private BearerToken() {}

    public static String extractFrom(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new InvalidSessionException();
        }
        return authorization.substring(BEARER_PREFIX.length()).trim();
    }
}
