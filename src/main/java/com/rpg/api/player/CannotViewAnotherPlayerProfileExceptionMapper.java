package com.rpg.api.player;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.player.exceptions.CannotViewAnotherPlayerProfileException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class CannotViewAnotherPlayerProfileExceptionMapper
        implements ExceptionMapper<CannotViewAnotherPlayerProfileException> {

    @Override
    public Response toResponse(CannotViewAnotherPlayerProfileException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.code(), exception.getMessage()))
                .build();
    }
}
