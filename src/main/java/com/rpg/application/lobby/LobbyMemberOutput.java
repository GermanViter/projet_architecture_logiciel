package com.rpg.application.lobby;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.player.Pseudonym;

import java.util.Optional;

public record LobbyMemberOutput(String playerId, String pseudonym, Optional<String> selectedHero) {

    public static LobbyMemberOutput from(Pseudonym member) {
        return new LobbyMemberOutput(member.getValue(), member.getValue(), Optional.empty());
    }

    public static LobbyMemberOutput from(Pseudonym member, Hero hero) {
        return new LobbyMemberOutput(member.getValue(), member.getValue(), Optional.of(hero.name().getName()));
    }
}
