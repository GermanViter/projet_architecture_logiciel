package com.rpg.api.player;

import com.rpg.domain.player.InvalidPasswordException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class InvalidPasswordExceptionMapper implements ExceptionMapper<InvalidPasswordException> {
    @Override
    public Response toResponse(InvalidPasswordException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                .build();
    }
}
