package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.LobbyFullException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class LobbyFullExceptionMapper
        implements ExceptionMapper<LobbyFullException> {

    @Override
    public Response toResponse(LobbyFullException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("LOBBY_FULL", "Le lobby est plein."))
                .build();
    }
}
