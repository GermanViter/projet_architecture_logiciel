package com.rpg.api.lobby;

import com.rpg.application.lobby.LobbyMemberDetail;
import com.rpg.application.lobby.LobbySummary;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyMember;
import com.rpg.domain.lobby.LobbyStatus;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyMapper {
    public static LobbySummary toSummary(Lobby lobby) {
        return new LobbySummary(
                lobby.id().toString(),
                lobby.host().toString(),
                lobby.members().size(),
                4 // MAX_PLAYERS
        );
    }

    public static List<LobbySummary> toSummaries(List<Lobby> lobbies) {
        return lobbies.stream().map(LobbyMapper::toSummary).collect(Collectors.toList());
    }

    public static List<LobbyMemberDetail> toMemberDetails(List<LobbyMember> members) {
        return members.stream()
                .map(m -> new LobbyMemberDetail(
                        m.pseudonym().toString(),
                        m.hero() != null ? m.hero().id().toString() : null
                ))
                .collect(Collectors.toList());
    }
}
