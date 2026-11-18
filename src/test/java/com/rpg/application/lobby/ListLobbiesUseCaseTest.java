package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListLobbiesUseCaseTest {
    private LobbyRepository lobbyRepository;
    private ListLobbiesUseCase listLobbiesUseCase;

    @BeforeEach
    void setUp() {
        lobbyRepository = mock(LobbyRepository.class);
        listLobbiesUseCase = new ListLobbiesUseCase(lobbyRepository);
    }

    @Test
    void execute_ShouldReturnLobbiesWithAvailableSeatsAndOpenStatus() {
        Pseudonym host = new Pseudonym("host1");
        Lobby lobby1 = Lobby.openedBy(host);
        lobby1.join(new Pseudonym("player1"));

        Lobby lobby2 = Lobby.openedBy(new Pseudonym("host2"));


        Lobby lobby3 = Lobby.openedBy(new Pseudonym("host3"));

        lobby3.join(new Pseudonym("player2"));
        lobby3.join(new Pseudonym("player3"));
        lobby3.join(new Pseudonym("player4"));

        when(lobbyRepository.findAll()).thenReturn(List.of(lobby1, lobby3));

        ListLobbiesOutput output = listLobbiesUseCase.execute();

        assertEquals(1, output.lobbies().size());
        assertEquals(lobby1.id().getValue(), output.lobbies().get(0).id());
    }
}