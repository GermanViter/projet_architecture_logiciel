package com.rpg.api.player;

import com.rpg.application.player.CreatePlayerCommand;
import com.rpg.application.player.CreatePlayerOutput;
import com.rpg.application.player.CreatePlayerUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/players")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class PlayerResource {
    private final CreatePlayerUseCase createPlayer;

    @Inject
    public PlayerResource(CreatePlayerUseCase createPlayer) {
        this.createPlayer = createPlayer;
    }

    @POST
    public Response createPlayer(@Valid @NotNull CreatePlayerRequest request) {
        CreatePlayerOutput output = createPlayer.execute(
                new CreatePlayerCommand(request.pseudonym, request.email, request.password));

        return Response
                .created(UriBuilder.fromResource(PlayerResource.class)
                        .path(output.pseudonym())
                        .build())
                .build();
    }
}
