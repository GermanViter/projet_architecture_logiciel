package com.rpg.application.player;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Password;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.Pseudonym;
import com.rpg.domain.player.exceptions.ForbiddenException;
import com.rpg.domain.player.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.junit.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class GetPlayerProfileUseCaseTest {

    @Mock
    private PlayerRepository repository;

    private final GetPlayerProfileUseCase useCase = new GetPlayerProfileUseCase(repository);

    @Test
    void execute_validId_sameUser_returnsOutput() {
        // Given
        String playerId = "p-1";
        Email email = new Email("aria@mail.com");
        Player player = new Player(new Pseudonym("Aria"), email, new Password("secret"));

        when(repository.findById(playerId)).thenReturn(Optional.of(player));

        // When
        PlayerProfileOutput output = useCase.execute(playerId, email);

        // Then
        assertThat(output.id()).isEqualTo(playerId);
        assertThat(output.pseudonym()).isEqualTo("Aria");
        assertThat(output.email()).isEqualTo("aria@mail.com");
        verify(repository).findById(playerId);
    }

    @Test
    void execute_nonExistentId_throwsPlayerNotFound() {
        // Given
        String playerId = "invalid";
        when(repository.findById(playerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PlayerNotFoundException.class, () -> useCase.execute(playerId, new Email("any@mail.com")));
    }

    @Test
    void execute_differentUser_throwsForbidden() {
        // Given
        String playerId = "p-1";
        Email ownerEmail = new Email("aria@mail.com");
        Email requesterEmail = new Email("bob@mail.com");
        Player player = new Player(new Pseudonym("Aria"), ownerEmail, new Password("secret"));

        when(repository.findById(playerId)).thenReturn(Optional.of(player));

        // When & Then
        assertThrows(ForbiddenException.class, () -> useCase.execute(playerId, requesterEmail));
    }
}