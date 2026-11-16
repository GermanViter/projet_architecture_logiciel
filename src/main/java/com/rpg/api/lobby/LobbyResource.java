package com.rpg.api.lobby;

import com.rpg.application.lobby.CreateLobbyCommand;
import com.rpg.application.lobby.CreateLobbyOutput;
import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.domain.session.exceptions.InvalidSessionException;
import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/lobbies")
@Produces(MediaType.APPLICATION_JSON)
public final class LobbyResource {
    private static final String BEARER_PREFIX = "Bearer ";

    private final CreateLobbyUseCase createLobby;

    @Inject
    public LobbyResource(CreateLobbyUseCase createLobby) {
        this.createLobby = createLobby;
    }

    @POST
    public Response createLobby(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        CreateLobbyOutput output =
                createLobby.execute(new CreateLobbyCommand(extractBearerToken(authorization)));

        return Response
                .created(UriBuilder.fromResource(LobbyResource.class)
                        .path(output.lobbyId())
                        .build())
                .build();
    }

    private static String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new InvalidSessionException();
        }
        return authorization.substring(BEARER_PREFIX.length()).trim();
    }
}
