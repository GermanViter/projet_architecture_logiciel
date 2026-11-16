package com.rpg.api.lobby;

import com.rpg.domain.lobby.exceptions.PlayerAlreadyInLobbyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class PlayerAlreadyInLobbyExceptionMapper
        implements ExceptionMapper<PlayerAlreadyInLobbyException> {

    @Override
    public Response toResponse(PlayerAlreadyInLobbyException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"ALREADY_IN_LOBBY\"}")
                .build();
    }
}
