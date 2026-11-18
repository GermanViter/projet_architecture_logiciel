package com.rpg.application.lobby;

import com.rpg.domain.lobby.LobbyMember;

public record LobbyMemberDetail(
        String pseudonym,
        String heroName
) {
    public static LobbyMemberDetail from(LobbyMember member) {
        return new LobbyMemberDetail(
                member.pseudonym().getValue(),
                member.hero() != null ? member.hero().name().getName() : null
        );
    }
}