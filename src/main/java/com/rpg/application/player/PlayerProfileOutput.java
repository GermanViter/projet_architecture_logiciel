package com.rpg.application.player;

import com.rpg.domain.player.Player;

public record PlayerProfileOutput(String id, String pseudonym, String email) {

    public static PlayerProfileOutput from(Player player) {
        return new PlayerProfileOutput(
                player.pseudonym().getValue(),
                player.pseudonym().getValue(),
                player.email().getValue());
    }
}
