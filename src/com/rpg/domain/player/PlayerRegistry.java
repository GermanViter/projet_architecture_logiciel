package com.rpg.domain.player;

import java.util.ArrayList;
import java.util.List;

public final class PlayerRegistry {
    private final List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        if (pseudonymAlreadyExists(player.pseudonym())) {
            throw new DuplicatePlayerException("pseudonym", player.pseudonym().getValue());
        }
        if (emailAlreadyExists(player.email())) {
            throw new DuplicatePlayerException("email", player.email().getValue());
        }
        players.add(player);
    }

    private boolean pseudonymAlreadyExists(Pseudonym pseudonym) {
        return players.stream().anyMatch(p -> p.pseudonym().equals(pseudonym));
    }

    private boolean emailAlreadyExists(Email email) {
        return players.stream().anyMatch(p -> p.email().equals(email));
    }

    public List<Player> allPlayers() {
        return new ArrayList<>(players);
    }
}
