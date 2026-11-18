package com.rpg.application.lobby;

public record SelectHeroCommand(String token, String lobbyId, String playerId, String heroName) {}
