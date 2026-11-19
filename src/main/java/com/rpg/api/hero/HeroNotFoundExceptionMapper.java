package com.rpg.api.hero;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.hero.exceptions.HeroNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class HeroNotFoundExceptionMapper
        implements ExceptionMapper<HeroNotFoundException> {
    private static final String ERROR_CODE = "HERO_NOT_FOUND";

    @Override
    public Response toResponse(HeroNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(ERROR_CODE, exception.getMessage()))
                .build();
    }
}
