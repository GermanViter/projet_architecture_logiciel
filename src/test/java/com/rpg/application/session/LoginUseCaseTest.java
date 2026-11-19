package com.rpg.application.session;

import com.rpg.domain.player.Email;
import com.rpg.domain.player.Player;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.player.exceptions.InvalidCredentialsException;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import com.rpg.domain.player.fixtures.EmailFixture;
import com.rpg.domain.player.fixtures.PasswordFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static com.rpg.domain.player.fixtures.PlayerFixture.aPlayer;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private PlayerRepository players;

    @Mock
    private SessionRepository sessions;

    @InjectMocks
    private LoginUseCase useCase;

    @Test
    void execute_validCredentials_returnsToken() {
        Player player = aPlayer();
        when(players.findByEmail(any())).thenReturn(Optional.of(player));

        LoginOutput output = useCase.execute(aValidCommand());

        assertThat(output.token()).isNotEmpty();
    }

    @Test
    void execute_validCredentials_savesSessionInRepository() {
        Player player = aPlayer();
        when(players.findByEmail(any())).thenReturn(Optional.of(player));

        useCase.execute(aValidCommand());

        verify(sessions).addSession(any(SessionToken.class), any(Email.class));
    }

    @Test
    void execute_unknownEmail_throwsInvalidCredentials() {
        when(players.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(aValidCommand()));
    }

    @Test
    void execute_wrongPassword_throwsInvalidCredentials() {
        Player player = aPlayer();
        when(players.findByEmail(any())).thenReturn(Optional.of(player));

        LoginCommand command = new LoginCommand(EmailFixture.VALID_VALUE, "wrongpassword");

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(command));
    }

    @Test
    void execute_malformedEmail_throwsInvalidCredentials() {
        LoginCommand command = new LoginCommand("notanemail", PasswordFixture.VALID_VALUE);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(command));
    }

    private static LoginCommand aValidCommand() {
        return new LoginCommand(EmailFixture.VALID_VALUE, PasswordFixture.VALID_VALUE);
    }
}
