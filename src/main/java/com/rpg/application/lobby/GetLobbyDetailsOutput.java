package com.rpg.application.lobby;

import java.util.List;

public record GetLobbyDetailsOutput(
        String hostId,
        String status,
        List<LobbyMemberDetail> members
) {}