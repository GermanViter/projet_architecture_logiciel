
package com.rpg.domain.player;

import java.util.Optional;

public interface PlayerRepository {
    void addPlayer(Player player);

    Optional<Player> findByEmail(Email email);

    boolean exists(Pseudonym pseudonym);
}
