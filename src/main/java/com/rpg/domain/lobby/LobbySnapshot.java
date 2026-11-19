package com.rpg.domain.lobby;

import com.rpg.domain.player.Pseudonym;

import java.util.List;
import java.util.Optional;

public record LobbySnapshot(
        Optional<Pseudonym> host,
        LobbyStatus status,
        List<LobbyMember> members
) {}
