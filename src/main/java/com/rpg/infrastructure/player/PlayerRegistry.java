package com.rpg.infrastructure.player;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.Pseudonym;
import com.rpg.domain.player.exceptions.DuplicatePlayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PlayerRegistry implements PlayerRepository {
    private final List<Player> players = new ArrayList<>();

    @Override
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

    @Override
    public Optional<Player> findByEmail(Email email) {
        return players.stream().filter(p -> p.email().equals(email)).findFirst();
    }

    public List<Player> allPlayers() {
        return new ArrayList<>(players);
    }
    @Override
    public boolean exists(Pseudonym pseudonym) {
        return players.stream()
                .anyMatch(player -> player.pseudonym().equals(pseudonym));
    }
}
