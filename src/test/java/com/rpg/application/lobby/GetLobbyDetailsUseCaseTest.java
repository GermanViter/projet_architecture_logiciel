package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.player.Pseudonym;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetLobbyDetailsUseCaseTest {
    private LobbyRepository lobbyRepository;
    private GetLobbyDetailsUseCase getLobbyDetailsUseCase;

    @BeforeEach
    void setUp() {
        lobbyRepository = mock(LobbyRepository.class);
        getLobbyDetailsUseCase = new GetLobbyDetailsUseCase(lobbyRepository);
    }

    @Test
    void execute_ShouldReturnLobbyDetails() {
        Pseudonym host = new Pseudonym("host");
        Lobby lobby = Lobby.openedBy(host);
        lobby.join(new Pseudonym("player1"));

        when(lobbyRepository.findById(any(LobbyId.class))).thenReturn(Optional.of(lobby));

        GetLobbyDetailsOutput output = getLobbyDetailsUseCase.execute("any-id");

        assertEquals("host", output.hostId());
        assertEquals("OPEN", output.status());
        assertEquals(2, output.members().size());
    }

    @Test
    void execute_ShouldThrowExceptionWhenLobbyNotFound() {
        when(lobbyRepository.findById(any(LobbyId.class))).thenReturn(Optional.empty());

        assertThrows(com.rpg.domain.lobby.exceptions.LobbyNotFoundException.class, () -> {
            getLobbyDetailsUseCase.execute("invalid-id");
        });
    }
}
