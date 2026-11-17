package com.rpg.application.lobby;

import java.util.List;

public record ListLobbiesOutput(
    List<LobbySummary> lobbies
) {}

public record LobbySummary(
    String id,
    String hostId,
    int playerCount,
    int maxPlayers
) {}
