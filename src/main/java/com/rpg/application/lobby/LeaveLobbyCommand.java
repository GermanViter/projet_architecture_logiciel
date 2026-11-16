package com.rpg.application.lobby;

public record LeaveLobbyCommand(String token, String lobbyId, String playerId) {}
