package com.rpg.application.lobby;

import java.util.List;

public record ListLobbiesOutput(
    List<LobbySummary> lobbies
) {}
