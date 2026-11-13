package com.rpg.api.lobby;

import com.rpg.application.lobby.CreateLobbyUseCase;
import com.rpg.domain.lobby.Lobby;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.UUID;

@Path("/lobbies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class LobbyResource {
    private final CreateLobbyUseCase createLobby;

    @Inject
    public LobbyResource(CreateLobbyUseCase createLobby) {
        this.createLobby = createLobby;
    }

    @POST
    public Response createLobby(@Valid CreateLobbyRequest request) {
        try {
            Lobby lobby = createLobby.execute(request.pseudonym);

            return Response.created(
                    UriBuilder.fromResource(LobbyResource.class)
                            .path(lobby.id().toString())
                            .build()
            ).entity(new LobbyResponse(lobby.id(), lobby.host().getValue())).build();

        } catch (CreateLobbyUseCase.PlayerNotFoundException exception) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(exception.getMessage())
                    .build();
        }
    }

    public static final class CreateLobbyRequest {
        @NotBlank
        @Size(min = 3, max = 20)
        public String pseudonym;
    }

    public record LobbyResponse(UUID id, String host) {}
}