package com.rpg.api.lobby;

import com.rpg.application.lobby.LobbyMemberOutput;

public record LobbyMemberResponse(String playerId, String pseudonym) {

    public static LobbyMemberResponse from(LobbyMemberOutput member) {
        return new LobbyMemberResponse(member.playerId(), member.pseudonym());
    }
}
