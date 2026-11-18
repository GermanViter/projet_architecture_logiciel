package com.rpg.application.lobby;

public record LobbySummary(
    String id,
    String hostId,
    int playerCount,
    int maxPlayers
) {}
