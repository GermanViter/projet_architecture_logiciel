package com.rpg.api.player;

import com.rpg.application.player.GetPlayerProfileUseCase;
import com.rpg.domain.player.Email;
import com.rpg.domain.session.SessionRepository;
import com.rpg.domain.session.SessionToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/players")
public final class PlayerProfileResource {
    private final GetPlayerProfileUseCase getPlayerProfileUseCase;
    private final SessionRepository sessionRepository;

    public PlayerProfileResource(GetPlayerProfileUseCase getPlayerProfileUseCase, SessionRepository sessionRepository) {
        this.getPlayerProfileUseCase = getPlayerProfileUseCase;
        this.sessionRepository = sessionRepository;
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

        String tokenValue = auth.substring(7);
        SessionToken token = SessionToken.of(tokenValue);

        return sessionRepository.findEmailByToken(token)
                .map(email -> Response.ok(getPlayerProfileUseCase.execute(playerId, email)).build())
                .orElse(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"INVALID_SESSION\"}")
                        .build());
    }
}
