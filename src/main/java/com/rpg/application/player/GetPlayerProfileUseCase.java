package com.rpg.application.player;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;

public final class GetPlayerProfileUseCase {
    private final PlayerRepository repository;

    public GetPlayerProfileUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public PlayerProfileOutput execute(String playerId, Email authenticatedUserEmail) {
        Player player = repository.findById(playerId)
            .orElseThrow(() -> new com.rpg.domain.player.exceptions.PlayerNotFoundException(playerId));

        if (!player.email().equals(authenticatedUserEmail)) {
            throw new com.rpg.domain.player.exceptions.ForbiddenException();
        }

        return new PlayerProfileOutput(
            playerId,
            player.pseudonym().value(),
            player.email().value()
        );
    }
}
