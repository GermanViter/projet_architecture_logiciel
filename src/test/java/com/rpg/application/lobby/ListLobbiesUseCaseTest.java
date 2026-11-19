package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.MAX_PLAYERS;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobbyWithMembers;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.aPseudonym;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListLobbiesUseCaseTest {
    private static final int ONLY_THE_HOST = 1;

    @Mock
    private LobbyRepository lobbies;

    private ListLobbiesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListLobbiesUseCase(lobbies);
    }

    @Test
    void execute_lobbyWithOneSeatLeft_isListed() {
        when(lobbies.findAll()).thenReturn(List.of(aLobbyWithMembers(MAX_PLAYERS - 1)));

        ListLobbiesOutput output = useCase.execute();

        assertThat(output.lobbies()).hasSize(1);
    }

    @Test
    void execute_fullLobby_isNotListed() {
        when(lobbies.findAll()).thenReturn(List.of(aLobbyWithMembers(MAX_PLAYERS)));

        ListLobbiesOutput output = useCase.execute();

        assertThat(output.lobbies()).isEmpty();
    }

    @Test
    void execute_lobbyLeftByItsLastMember_isNotListed() {
        Lobby lobby = aLobby();
        lobby.leave(aPseudonym());
        when(lobbies.findAll()).thenReturn(List.of(lobby));

        ListLobbiesOutput output = useCase.execute();

        assertThat(output.lobbies()).isEmpty();
    }

    @Test
    void execute_joinableLobby_isSummarizedWithItsIdHostAndPlayerCount() {
        Lobby lobby = aLobby();
        when(lobbies.findAll()).thenReturn(List.of(lobby));

        ListLobbiesOutput output = useCase.execute();

        assertThat(output.lobbies()).containsExactly(
                new LobbySummary(lobby.id().getValue(), VALID_VALUE, ONLY_THE_HOST, MAX_PLAYERS));
    }
}
