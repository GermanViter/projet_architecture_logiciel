package com.rpg.api.lobby;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.lobby.exceptions.HeroNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class HeroNotFoundExceptionMapper
        implements ExceptionMapper<HeroNotFoundException> {

    @Override
    public Response toResponse(HeroNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.code(), exception.getMessage()))
                .build();
    }
}
