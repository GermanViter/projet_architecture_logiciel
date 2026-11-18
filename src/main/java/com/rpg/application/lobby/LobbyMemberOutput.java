package com.rpg.application.lobby;

import com.rpg.domain.hero.HeroName;
import com.rpg.domain.lobby.LobbyMember;

public record LobbyMemberOutput(String playerId, String pseudonym, String selectedHero) {

    public static LobbyMemberOutput from(LobbyMember member) {
        String pseudonym = member.pseudonym().getValue();
        return new LobbyMemberOutput(
                pseudonym,
                pseudonym,
                member.selectedHeroName().map(HeroName::getName).orElse(null));
    }
}
