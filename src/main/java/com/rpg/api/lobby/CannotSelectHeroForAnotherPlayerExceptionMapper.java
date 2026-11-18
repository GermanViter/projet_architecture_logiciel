package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.CannotSelectHeroForAnotherPlayerException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class CannotSelectHeroForAnotherPlayerExceptionMapper
        implements ExceptionMapper<CannotSelectHeroForAnotherPlayerException> {

    @Override
    public Response toResponse(CannotSelectHeroForAnotherPlayerException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.code(), exception.getMessage()))
                .build();
    }
}
