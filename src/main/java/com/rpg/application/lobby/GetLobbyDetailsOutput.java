package com.rpg.application.lobby;

import com.rpg.domain.lobby.LobbyStatus;
import java.util.List;

public record GetLobbyDetailsOutput(
    String hostPseudonym,
    LobbyStatus status,
    List<LobbyMemberDetail> members
) {}

public record LobbyMemberDetail(
    String pseudonym,
    String heroId
) {}
