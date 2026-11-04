package com.rpg.api.player;

import com.rpg.domain.player.exceptions.InvalidPseudonymException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class InvalidPseudonymExceptionMapper implements ExceptionMapper<InvalidPseudonymException> {
    @Override
    public Response toResponse(InvalidPseudonymException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                .build();
    }
}
