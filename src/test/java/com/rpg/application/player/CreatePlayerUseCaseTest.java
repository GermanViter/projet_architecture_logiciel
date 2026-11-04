package com.rpg.application.player;

import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.exceptions.DuplicatePlayerException;
import com.rpg.domain.player.exceptions.InvalidEmailException;
import com.rpg.domain.player.exceptions.InvalidPasswordException;
import com.rpg.domain.player.exceptions.InvalidPseudonymException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.EmailFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.MIN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CreatePlayerUseCaseTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private CreatePlayerUseCase useCase;

    @Test
    void execute_validCommand_returnsCreatedPseudonym() {
        CreatePlayerCommand command = aValidCommand();

        CreatePlayerOutput output = useCase.execute(command);

        assertThat(output.pseudonym()).isEqualTo(command.pseudonym());
    }

    @Test
    void execute_validCommand_savesPlayerInRepository() {
        useCase.execute(aValidCommand());

        verify(playerRepository).addPlayer(any());
    }

    @Test
    void execute_invalidPseudonym_isRefusedAndNothingIsSaved() {
        String belowMinimum = "a".repeat(MIN_LENGTH - 1);
        CreatePlayerCommand command = new CreatePlayerCommand(belowMinimum, VALID_VALUE, "secret123");

        assertThrows(InvalidPseudonymException.class,
                () -> useCase.execute(command));

        verifyNoInteractions(playerRepository);
    }

    @Test
    void execute_invalidEmail_isRefusedAndNothingIsSaved() {
        CreatePlayerCommand command = new CreatePlayerCommand("Alice", "notanemail", "secret123");

        assertThrows(InvalidEmailException.class,
                () -> useCase.execute(command));

        verifyNoInteractions(playerRepository);
    }

    @Test
    void execute_invalidPassword_isRefusedAndNothingIsSaved() {
        CreatePlayerCommand command = new CreatePlayerCommand("Alice", VALID_VALUE, "short");

        assertThrows(InvalidPasswordException.class,
                () -> useCase.execute(command));

        verifyNoInteractions(playerRepository);
    }

    @Test
    void execute_duplicatePseudonym_isRefused() {
        CreatePlayerCommand command = new CreatePlayerCommand("Alice", VALID_VALUE, "secret123");
        org.mockito.Mockito.doThrow(new DuplicatePlayerException("pseudonym", "Alice"))
                .when(playerRepository).addPlayer(any());

        assertThrows(DuplicatePlayerException.class,
                () -> useCase.execute(command));
    }

    private static CreatePlayerCommand aValidCommand() {
        return new CreatePlayerCommand("Alice", VALID_VALUE, "secret123");
    }
}
