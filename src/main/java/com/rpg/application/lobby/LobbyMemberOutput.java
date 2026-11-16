package com.rpg.application.lobby;

import com.rpg.domain.player.Pseudonym;

public record LobbyMemberOutput(String playerId, String pseudonym) {

    public static LobbyMemberOutput from(Pseudonym member) {
        return new LobbyMemberOutput(member.getValue(), member.getValue());
    }
}
