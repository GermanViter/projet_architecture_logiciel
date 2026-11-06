package com.rpg.application.player;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Password;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.Pseudonym;

public final class CreatePlayerUseCase {
    private final PlayerRepository playerRegistry;

    public CreatePlayerUseCase(PlayerRepository playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    public CreatePlayerOutput execute(CreatePlayerCommand command) {
        Pseudonym pseudonym = new Pseudonym(command.pseudonym());
        Email email = new Email(command.email());
        Password password = new Password(command.password());

        Player player = new Player(pseudonym, email, password);
        playerRegistry.addPlayer(player);

        return new CreatePlayerOutput(pseudonym.getValue());
    }
}
