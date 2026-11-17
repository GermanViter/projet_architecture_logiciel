package com.rpg.api.lobby;

public record SelectHeroRequest(String heroName) {

    public SelectHeroRequest {
        if (heroName == null || heroName.isBlank()) {
            throw new IllegalArgumentException("Hero name must not be empty.");
        }
    }
}
