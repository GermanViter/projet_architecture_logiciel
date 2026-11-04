package com.rpg.application.player;

import com.rpg.domain.player.PlayerRegistry;
import com.rpg.domain.player.exceptions.DuplicatePlayerException;
import com.rpg.domain.player.exceptions.InvalidEmailException;
import com.rpg.domain.player.exceptions.InvalidPasswordException;
import com.rpg.domain.player.exceptions.InvalidPseudonymException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.EmailFixture.VALID_VALUE;
import static com.rpg.domain.player.fixtures.PseudonymFixture.MIN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreatePlayerUseCaseTest {

    private final PlayerRegistry registry = new PlayerRegistry();
    private final CreatePlayerUseCase useCase = new CreatePlayerUseCase(registry);

    @Test
    void execute_validCommand_returnsCreatedPseudonym() {
        CreatePlayerCommand command = aValidCommand();

        CreatePlayerOutput output = useCase.execute(command);

        assertThat(output.pseudonym()).isEqualTo(command.pseudonym());
    }

    @Test
    void execute_validCommand_savesPlayerInRegistry() {
        CreatePlayerCommand command = aValidCommand();

        useCase.execute(command);

        assertThat(registry.allPlayers()).hasSize(1);
        assertThat(registry.allPlayers().get(0).pseudonym().getValue())
                .isEqualTo(command.pseudonym());
    }

    @Test
    void execute_invalidPseudonym_isRefusedAndNothingIsSaved() {
        String belowMinimum = "a".repeat(MIN_LENGTH - 1);
        CreatePlayerCommand command = new CreatePlayerCommand(belowMinimum, VALID_VALUE, "secret123");

        assertThrows(InvalidPseudonymException.class,
                () -> useCase.execute(command));

        assertThat(registry.allPlayers()).isEmpty();
    }

    @Test
    void execute_invalidEmail_isRefusedAndNothingIsSaved() {
        CreatePlayerCommand command = new CreatePlayerCommand("Alice", "notanemail", "secret123");

        assertThrows(InvalidEmailException.class,
                () -> useCase.execute(command));

        assertThat(registry.allPlayers()).isEmpty();
    }

    @Test
    void execute_invalidPassword_isRefusedAndNothingIsSaved() {
        CreatePlayerCommand command = new CreatePlayerCommand("Alice", VALID_VALUE, "short");

        assertThrows(InvalidPasswordException.class,
                () -> useCase.execute(command));

        assertThat(registry.allPlayers()).isEmpty();
    }

    @Test
    void execute_duplicatePseudonym_isRefused() {
        useCase.execute(aValidCommand());
        CreatePlayerCommand duplicate = new CreatePlayerCommand("Alice", "other@example.com", "secret123");

        assertThrows(DuplicatePlayerException.class,
                () -> useCase.execute(duplicate));
    }

    @Test
    void execute_duplicateEmail_isRefused() {
        useCase.execute(aValidCommand());
        CreatePlayerCommand duplicate = new CreatePlayerCommand("Bob", VALID_VALUE, "secret123");

        assertThrows(DuplicatePlayerException.class,
                () -> useCase.execute(duplicate));
    }

    private static CreatePlayerCommand aValidCommand() {
        return new CreatePlayerCommand("Alice", VALID_VALUE, "secret123");
    }
}
