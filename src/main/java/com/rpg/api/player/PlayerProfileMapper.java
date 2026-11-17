package com.rpg.api.player;

import com.rpg.application.player.PlayerProfileOutput;
import com.rpg.domain.player.Player;

public class PlayerProfileMapper {
    public static PlayerProfileOutput toOutput(Player player, String playerId) {
        return new PlayerProfileOutput(
            playerId,
            player.pseudonym().value(),
            player.email().value()
        );
    }
}
