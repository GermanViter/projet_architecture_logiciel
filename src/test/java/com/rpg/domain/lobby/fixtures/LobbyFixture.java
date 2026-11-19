package com.rpg.domain.lobby.fixtures;

import com.rpg.domain.hero.fixtures.HeroFixture;
import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyMember;
import com.rpg.domain.player.Pseudonym;
import com.rpg.domain.player.fixtures.PseudonymFixture;

import java.util.List;

public final class LobbyFixture {
    public static final int MIN_PLAYERS = 1;
    public static final int MAX_PLAYERS = 4;
    public static final String UNKNOWN_LOBBY_ID = "an-unknown-lobby";
    private static final String GUEST_PSEUDONYM_PREFIX = "Guest";

    private LobbyFixture() {}

    public static Lobby aLobby() {
        return Lobby.openedBy(PseudonymFixture.aPseudonym());
    }

    public static Lobby aLobbyHostedBy(String pseudonym) {
        return Lobby.openedBy(new Pseudonym(pseudonym));
    }

    public static Lobby aLobbyWithMembers(int memberCount) {
        Lobby lobby = aLobby();
        for (int guest = 1; guest < memberCount; guest++) {
            lobby.join(new Pseudonym(GUEST_PSEUDONYM_PREFIX + guest));
        }
        return lobby;
    }

    public static Lobby aLobbyReadyToStartWithMembers(int memberCount) {
        Lobby lobby = aLobbyWithMembers(memberCount);
        List<LobbyMember> members = lobby.members();
        for (int seat = 0; seat < members.size(); seat++) {
            lobby.selectHero(members.get(seat).pseudonym(),
                    HeroFixture.aHeroNamed(HeroFixture.DISTINCT_HERO_NAMES.get(seat)));
        }
        return lobby;
    }
}
