package com.rpg.application.lobby;

import com.rpg.domain.lobby.Lobby;
import com.rpg.domain.lobby.LobbyId;
import com.rpg.domain.lobby.LobbyRepository;
import com.rpg.domain.lobby.LobbyStatus;
import com.rpg.domain.lobby.exceptions.LobbyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.UNKNOWN_LOBBY_ID;
import static com.rpg.domain.lobby.fixtures.LobbyFixture.aLobby;
import static com.rpg.domain.player.fixtures.PseudonymFixture.OTHER_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.anotherPseudonym;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLobbyDetailsUseCaseTest {
    private static final String NO_HERO_SELECTED = null;

    @Mock
    private LobbyRepository lobbies;

    private GetLobbyDetailsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetLobbyDetailsUseCase(lobbies);
    }

    @Test
    void execute_storedLobby_returnsItsHostStatusAndMembers() {
        Lobby lobby = aLobby();
        lobby.join(anotherPseudonym());
        when(lobbies.findById(lobby.id())).thenReturn(Optional.of(lobby));

        GetLobbyDetailsOutput output = useCase.execute(lobby.id().getValue());

        assertThat(output).isEqualTo(new GetLobbyDetailsOutput(
                VALID_VALUE,
                LobbyStatus.OPEN.name(),
                List.of(new LobbyMemberDetail(VALID_VALUE, NO_HERO_SELECTED),
                        new LobbyMemberDetail(OTHER_VALUE, NO_HERO_SELECTED))));
    }

    @Test
    void execute_unknownLobby_isRefused() {
        when(lobbies.findById(LobbyId.of(UNKNOWN_LOBBY_ID))).thenReturn(Optional.empty());

        assertThrows(LobbyNotFoundException.class, () -> useCase.execute(UNKNOWN_LOBBY_ID));
    }
}
