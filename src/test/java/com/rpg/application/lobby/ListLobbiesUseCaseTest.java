package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        Pseudonym host = new Pseudonym("host");
        Lobby lobby1 = Lobby.openedBy(host);
        lobby1.join(new Pseudonym("player1"));

        Lobby lobby2 = Lobby.openedBy(new Pseudonym("host2"));
        lobby2.setStatus(LobbyStatus.CLOSED);

        Lobby lobby3 = Lobby.openedBy(new Pseudonym("host3"));
        // lobby3 is full (max 4)
        for (int i = 0; i < 4; i++) {
            lobby3.join(new Pseudonym("p" + i));
        }

        when(lobbyRepository.findAll()).thenReturn(List.of(lobby1, lobby2, lobby3));

        ListLobbiesOutput output = listLobbiesUseCase.execute();

        assertEquals(1, output.lobbies().size());
        assertEquals(lobby1.id().toString(), output.lobbies().get(0).id());
    }
}
