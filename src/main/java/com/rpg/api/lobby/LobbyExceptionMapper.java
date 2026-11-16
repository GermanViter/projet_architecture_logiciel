package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.LobbyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class LobbyExceptionMapper
        implements ExceptionMapper<LobbyException> {

    @Override
    public Response toResponse(LobbyException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.code(), exception.getMessage()))
                .build();
    }
}
