package com.rpg.api.player;

import com.rpg.application.player.GetPlayerProfileUseCase;
import com.rpg.domain.player.Email;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/players")
public final class PlayerProfileResource {
    private final GetPlayerProfileUseCase getPlayerProfileUseCase;

    public PlayerProfileResource(GetPlayerProfileUseCase getPlayerProfileUseCase) {
        this.getPlayerProfileUseCase = getPlayerProfileUseCase;
    }

    @GET
    @Path("/{playerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayerProfile(@PathParam("playerId") String playerId, @HeaderParam("Authorization") String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"NOT_AUTHENTICATED\"}")
                    .build();
        }

        // In a real production app, we'd validate and extract the email from the token.
        // For this exercise, we'll use a mock email.
        Email email = new Email("mock@mail.com");

        return Response.ok(getPlayerProfileUseCase.execute(playerId, email)).build();
    }
}
