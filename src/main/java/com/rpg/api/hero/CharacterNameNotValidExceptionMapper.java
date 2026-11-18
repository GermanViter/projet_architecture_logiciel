package com.rpg.api.hero;

import com.rpg.api.ErrorResponse;
import com.rpg.domain.hero.exceptions.CharacterNameNotValidException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class CharacterNameNotValidExceptionMapper
        implements ExceptionMapper<CharacterNameNotValidException> {
    private static final String ERROR_CODE = "INVALID_HERO_NAME";

    @Override
    public Response toResponse(CharacterNameNotValidException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(ERROR_CODE, exception.getMessage()))
                .build();
    }
}
