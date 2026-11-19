package com.rpg.api;

import com.rpg.domain.session.exceptions.InvalidSessionException;

public final class BearerToken {
    private static final String BEARER_PREFIX = "Bearer ";

    private BearerToken() {}

    public static String extractFrom(String authorization) {
        if (authorization == null || !hasBearerScheme(authorization)) {
            throw new InvalidSessionException();
        }
        return authorization.substring(BEARER_PREFIX.length()).trim();
    }

    private static boolean hasBearerScheme(String authorization) {
        return authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length());
    }
}
