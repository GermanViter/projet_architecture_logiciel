package com.rpg.domain.lobby;

import com.rpg.domain.player.Pseudonym;

import java.util.List;
import java.util.Optional;

public record LobbySnapshot(
        LobbyId id,
        Optional<Pseudonym> host,
        LobbyStatus status,
        List<LobbyMember> members,
        int availableSeats,
        int capacity,
        boolean joinable
) {}
