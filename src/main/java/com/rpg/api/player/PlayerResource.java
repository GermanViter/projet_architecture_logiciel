package com.rpg.api.player;

import com.rpg.api.BearerToken;
import com.rpg.application.player.CreatePlayerCommand;
import com.rpg.application.player.CreatePlayerOutput;
import com.rpg.application.player.CreatePlayerUseCase;
import com.rpg.application.player.GetPlayerProfileCommand;
import com.rpg.application.player.GetPlayerProfileUseCase;
import com.rpg.application.player.PlayerProfileOutput;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
public final class PlayerResource {
    private static final String PLAYER_ID_PATH = "{playerId}";

    private final CreatePlayerUseCase createPlayer;
    private final GetPlayerProfileUseCase getPlayerProfile;

    @Inject
    public PlayerResource(CreatePlayerUseCase createPlayer, GetPlayerProfileUseCase getPlayerProfile) {
        this.createPlayer = createPlayer;
        this.getPlayerProfile = getPlayerProfile;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPlayer(@Valid @NotNull CreatePlayerRequest request) {
        CreatePlayerOutput output = createPlayer.execute(
                new CreatePlayerCommand(request.pseudonym, request.email, request.password));

        return Response
                .created(UriBuilder.fromResource(PlayerResource.class)
                        .path(output.pseudonym())
                        .build())
                .build();
    }

    @GET
    @Path(PLAYER_ID_PATH)
    public Response getPlayerProfile(@PathParam("playerId") String playerId,
                                     @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        PlayerProfileOutput output = getPlayerProfile.execute(
                new GetPlayerProfileCommand(BearerToken.extractFrom(authorization), playerId));

        return Response.ok(output).build();
    }
}
