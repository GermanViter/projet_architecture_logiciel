package com.rpg.api.lobby;

import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.application.lobby.GetLobbyDetailsUseCase;
import com.rpg.application.lobby.JoinLobbyUseCase;
import com.rpg.application.lobby.LeaveLobbyUseCase;
import com.rpg.application.lobby.ListLobbiesUseCase;
import com.rpg.application.session.ConnectedPlayerFinder;
import com.rpg.domain.player.PlayerRepository;
import com.rpg.domain.session.SessionRepository;
import com.rpg.infrastructure.lobby.LobbyRegistry;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class LobbyInstaller {
    private final PlayerRepository players;
    private final SessionRepository sessions;

    public LobbyInstaller(PlayerRepository players, SessionRepository sessions) {
        this.players = players;
        this.sessions = sessions;
    }

    public void install(ResourceConfig config) {
        LobbyRegistry lobbyRegistry = new LobbyRegistry();
        ConnectedPlayerFinder connectedPlayers = new ConnectedPlayerFinder(sessions, players);
        CreateLobbyUseCase createLobby = new CreateLobbyUseCase(connectedPlayers, lobbyRegistry);
        JoinLobbyUseCase joinLobby = new JoinLobbyUseCase(connectedPlayers, lobbyRegistry);
        LeaveLobbyUseCase leaveLobby = new LeaveLobbyUseCase(connectedPlayers, lobbyRegistry);
        ListLobbiesUseCase listLobbies = new ListLobbiesUseCase(lobbyRegistry);
        GetLobbyDetailsUseCase getLobbyDetails = new GetLobbyDetailsUseCase(lobbyRegistry);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createLobby).to(CreateLobbyUseCase.class);
                bind(joinLobby).to(JoinLobbyUseCase.class);
                bind(leaveLobby).to(LeaveLobbyUseCase.class);
                bind(listLobbies).to(ListLobbiesUseCase.class);
                bind(getLobbyDetails).to(GetLobbyDetailsUseCase.class);
            }
        });
        config.register(LobbyResource.class);
        config.register(new LobbyNotFoundExceptionMapper());
        config.register(new LobbyExceptionMapper());
        config.register(new CannotRemoveAnotherPlayerExceptionMapper());
        config.register(new PlayerNotInLobbyExceptionMapper());
    }
}
