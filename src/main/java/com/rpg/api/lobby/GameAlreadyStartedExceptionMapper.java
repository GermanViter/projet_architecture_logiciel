package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.GameAlreadyStartedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class GameAlreadyStartedExceptionMapper
        implements ExceptionMapper<GameAlreadyStartedException> {

    @Override
    public Response toResponse(GameAlreadyStartedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.code(), exception.getMessage()))
                .build();
    }
}
