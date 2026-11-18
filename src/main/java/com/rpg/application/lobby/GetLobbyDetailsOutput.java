package com.rpg.application.lobby;

import java.util.List;

public record GetLobbyDetailsOutput(
    String hostPseudonym,
    String status,
    List<LobbyMemberDetail> members
) {}
